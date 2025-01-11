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


    //Atributos para criar os dados para salvar no excel
    private static String caminho = "C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\salvar-marcações\\marcacoes-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
    private static org.apache.poi.ss.usermodel.Workbook workbook = null;
    public static Sheet sheet = null;
    public static int linha = 5;
    public static int contadorColunaNumero = 0;
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
                        "(sim) para caso tenha interesse na consulta.%n%n" +
                        "(não) para caso desistência da consulta :"));
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
                    "(sim) caso tenha interesse na consulta.%n%n OU %n%n" +
                    "(não) para caso desistência da consulta."));
        }


        if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario), "Está marcado, pode vim pegar no dia e horário que foi estipulado anteriormente.");
            whatsapp.removeListener(this);


            if (!nomeUsuariosUnico.contains(nomeUsuario)) {
                nomeUsuariosUnico.add(nomeUsuario);
                reabrirConecxao(this);
                executarPersistencia(this , "ACEITO");
            }

        }


        if (mensagemUsuario.equalsIgnoreCase("nao")
                || mensagemUsuario.equalsIgnoreCase("não")
                || mensagemUsuario.equalsIgnoreCase("naõ")
                || mensagemUsuario.equalsIgnoreCase("ñ")   ) {
            whatsapp.sendMessage(Jid.of(numeroUsuario), "Coloque o motivo da desistência abaixo : ");
            motivoDesistencia = true;
        }

    }


    private static void excelApi() throws IOException, InvalidFormatException {
        workbook = new XSSFWorkbook("C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\arquivo-planilha-cmce\\copia-para-usar-projeto.xlsx");
        sheet = workbook.getSheet("ESPECIALIDADES");
        //Row row = sheet.createRow(5);

//        Cell cabecalhoNome = row.createCell(0);
//        cabecalhoNome.setCellValue("Nome");
//        Cell cabecalhoNumero = row.createCell(1);
//        cabecalhoNumero.setCellValue("Numero");
//        Cell cabecalhoMotivo = row.createCell(2);
//        cabecalhoMotivo.setCellValue("Motivo");
//        Cell cabecalhoStatus = row.createCell(3);
//        cabecalhoStatus.setCellValue("Status");

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
            Row newRow = sheet.createRow(++linha);

            CellStyle cellStyleParaTodos = workbook.createCellStyle();
            Font fontParaTodos = workbook.createFont();
            fontParaTodos.setFontName("Aptos Narrow");
            cellStyleParaTodos.setFont(fontParaTodos);
            cellStyleParaTodos.setBorderBottom(BorderStyle.THIN);
            cellStyleParaTodos.setBorderRight(BorderStyle.THIN);
            cellStyleParaTodos.setBorderLeft(BorderStyle.THIN);
            cellStyleParaTodos.setBorderTop(BorderStyle.THIN);



            Cell cell0 = newRow.createCell(0);
            cell0.setCellValue(contadorColunaNumero++);
            cell0.setCellStyle(cellStyleParaTodos);

            Cell cell1 = newRow.createCell(1);
            cell1.setCellValue(listenerNovaMensagem.nomeUsuario);
            cell1.setCellStyle(cellStyleParaTodos);


            cellStyleParaTodos.setAlignment(HorizontalAlignment.CENTER);
            Cell cell2 = newRow.createCell(2);
            cell2.setCellValue("9"+listenerNovaMensagem.numeroUsuario.substring(5));
            cell2.setCellStyle(cellStyleParaTodos);




            Cell cell3 = newRow.createCell(4);
            CellStyle cellStyleCell3 = workbook.createCellStyle();
            cellStyleCell3.setBorderBottom(BorderStyle.THIN);
            cellStyleCell3.setBorderRight(BorderStyle.THIN);
            cellStyleCell3.setBorderLeft(BorderStyle.THIN);
            cellStyleCell3.setBorderTop(BorderStyle.THIN);
            Font font = workbook.createFont();
            if (!mensagemUsuario.equalsIgnoreCase("ACEITO")) {
                cellStyleCell3.setFillBackgroundColor(IndexedColors.RED1.getIndex());
                cellStyleCell3.setFillPattern(FillPatternType.DIAMONDS);
                cellStyleCell3.setAlignment(HorizontalAlignment.CENTER);
                font.setColor(IndexedColors.WHITE.getIndex());
                font.setFontName("Aptos Narrow");
                cellStyleCell3.setFont(font);
                cell3.setCellStyle(cellStyleCell3);

            }else {
                cellStyleCell3.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
                cellStyleCell3.setFillPattern(FillPatternType.DIAMONDS);
                cellStyleCell3.setAlignment(HorizontalAlignment.CENTER);
                font.setColor(IndexedColors.BLACK.getIndex());
                font.setFontName("Aptos Narrow");
                cellStyleCell3.setFont(font);
                cell3.setCellStyle(cellStyleCell3);
            }
            cell3.setCellValue(mensagemUsuario);


            Cell cabecalhoProcedimento= newRow.createCell(3);
            cabecalhoProcedimento.setCellValue("OFTALMOLOGISTA");
            cabecalhoProcedimento.setCellStyle(cellStyleParaTodos);

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



