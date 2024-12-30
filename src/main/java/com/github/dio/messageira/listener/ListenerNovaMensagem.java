package com.github.dio.messageira.listener;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.standard.TextMessage;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


@RegisterListener
@EnableAsync
public class ListenerNovaMensagem implements Listener {

    private Whatsapp whatsapp;
    private String nomeUsuario;
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;


    //Classes para criar os dados para salva no excel
    private static String caminho = "C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\salvar-marcações\\marcacoes-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
    private static org.apache.poi.ss.usermodel.Workbook workbook = null;
    public static Sheet sheet = null;
    public static int linha = 1;
    public static Set<String> nomeUsuariosUnico = new HashSet<String>();

    //Atributos para controlar a fila de Listener
    private static final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    public ListenerNovaMensagem(Whatsapp whatsapp, String nomeUsuario, String numeroUsuario) {
        this.whatsapp = whatsapp;
        this.nomeUsuario = nomeUsuario;
        this.numeroUsuario = "+" + numeroUsuario;
    }

    @SneakyThrows
    @Override
    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {
        String mensagemUsuario = null;
        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();
        if ((workbook == null) && (sheet == null)) {
            excelApi();
        }

        if (!jidNumeroUsuario.equals(numeroUsuario)) {
            return;
        }

        if (info.message().content() instanceof TextMessage textMessage) {
            mensagemUsuario = textMessage.text();
        }

        if (!(info.message().content() instanceof TextMessage textMessage)) {
            if (mensagemUsuario == null && jidNumeroUsuario.equals(numeroUsuario)) {
                whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("NÃO ACEITAMOS MENSAGEM DE AUDIO, FOTOS, VIDEOS OU FIGURINHAS COMO OPÇÃO.%n%n" +
                        "(sim) caso tenha interesse na consulta.%n%n" +
                        "(não) caso desistencia e depois digite o motivo da sua desistencia abaixo :"));
            }

            motivoDesistencia = false;
            return;
        }

        if (motivoDesistencia && info.message().content() instanceof TextMessage) {

            String motivo = mensagemUsuario;
            if (motivo.matches("[a-zA-Z0-9 À-ÿ.,!?]+")) {
                whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("MOTIVO : %S.%n%nMuito obrigado, o encaminhamento será arquivado e removido da fila.", motivo));
                motivoDesistencia = false;
                whatsapp.removeListener(this);

                if (!nomeUsuariosUnico.contains(nomeUsuario)) {
                    nomeUsuariosUnico.add(nomeUsuario);
                    reabrirConecxao(this);
                    executarPersistencia(this , motivo);
                }
                return;
            }
            motivoDesistencia = false;
        }


        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("Por favor digite uma das opções:%n%n" +
                    "(sim) caso tenha interesse na consulta.%n%n" +
                    "(não) caso desistencia e depois digite o motivo da sua desistencia abaixo :"));
        }


        if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario), "Está marcado, pode vim pegar no dia e horário que foi estipulado anteriomente.");
            whatsapp.removeListener(this);


            if (!nomeUsuariosUnico.contains(nomeUsuario)) {
                nomeUsuariosUnico.add(nomeUsuario);
                reabrirConecxao(this);
                executarPersistencia(this , "ACEITO");
            }

        }


        if (mensagemUsuario.equalsIgnoreCase("nao")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario), "Coloque o motivo da desistencia abaixo : ");
            motivoDesistencia = true;
        }

    }


    private static void excelApi() throws IOException, InvalidFormatException {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Marcações");
        Row row = sheet.createRow(0);

        Cell cabecalhoNome = row.createCell(0);
        cabecalhoNome.setCellValue("Nome");
        Cell cabecalhoNumero = row.createCell(1);
        cabecalhoNumero.setCellValue("Numero");
        Cell cabecalhoMotivo = row.createCell(2);
        cabecalhoMotivo.setCellValue("Motivo");
        Cell cabecalhoStatus = row.createCell(3);
        cabecalhoStatus.setCellValue("Status");

        try (FileOutputStream fileOutputStream = new FileOutputStream(caminho)) {
            workbook.write(new FileOutputStream(caminho));
            workbook.close();
        }

    }

    public static void reabrirConecxao(ListenerNovaMensagem listenerNovaMensagem) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(new File(caminho))) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheetAt(0);
            System.out.println("CONEXÃO ABERTA");

        }
    }


    private static CompletableFuture<Void> persistiDados(ListenerNovaMensagem listenerNovaMensagem , String mensagemUsuario) throws IOException {
        return CompletableFuture.runAsync(() -> {
            Row newRow = sheet.createRow(linha++);
            int col = 0;

            Cell cell = newRow.createCell(0);
            cell.setCellValue(listenerNovaMensagem.nomeUsuario);

            Cell cell2 = newRow.createCell(1);
            cell2.setCellValue(listenerNovaMensagem.numeroUsuario);

            Cell cell3 = newRow.createCell(2);
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            if (!mensagemUsuario.equalsIgnoreCase("ACEITO")) {
                cellStyle.setFillBackgroundColor(IndexedColors.RED1.getIndex());
                cellStyle.setFillPattern(FillPatternType.DIAMONDS);
                //cellStyle.setAlignment(HorizontalAlignment.CENTER);
                font.setColor(IndexedColors.WHITE.getIndex());
                cellStyle.setFont(font);
                cell3.setCellStyle(cellStyle);

            }else {
                cellStyle.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
                cellStyle.setFillPattern(FillPatternType.DIAMONDS);
                //cellStyle.setAlignment(HorizontalAlignment.CENTER);
                font.setColor(IndexedColors.BLACK.getIndex());
                cellStyle.setFont(font);
                cell3.setCellStyle(cellStyle);
            }
            cell3.setCellValue(mensagemUsuario);


            Cell cabecalhoStatus = newRow.createCell(3);
            cabecalhoStatus.setCellValue("..FALTA IMPLEMENTAR..");

            try (FileOutputStream fileOutputStream = new FileOutputStream(caminho)) {
                try {
                    workbook.write(fileOutputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("DADOS PERSISTIDO NA PLANILHA EXCEL");

        });


    }

    public static void executarPersistencia(ListenerNovaMensagem listenerNovaMensagem , String mensagem) {
        queue.add(() -> {
            try {
                persistiDados(listenerNovaMensagem , mensagem).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        if (!executor.isShutdown()) {
            executor.submit(() -> {
                while (!queue.isEmpty()) {
                    try {
                        Runnable task = queue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }

}



