package com.github.dio.messageira.listener;

import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.standard.TextMessage;
import it.auties.whatsapp.model.response.HasWhatsappResponse;
import jakarta.annotation.PostConstruct;
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
import java.util.UUID;
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
    private UUID uuidUnicoUsuario;
    private PacienteMR pacienteMR;

    //Atributos para criar os dados para salvar no excel
    private static String caminho = "C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\salvar-marcações\\marcacoes-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
    private static org.apache.poi.ss.usermodel.Workbook workbook = null;
    public static Sheet sheet = null;
    public static int linha = 5;
    public static int contadorColunaNumero = 0;
    public static Set<UUID> uuidUnicoUsuarioSet = new HashSet<UUID>();

    //Atributos para controlar a fila de Listener
    private static final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    public ListenerNovaMensagem(Whatsapp whatsapp, PacienteMR pacienteMR , String numeroUsuario) {
        this.whatsapp = whatsapp;
        this.nomeUsuario = pacienteMR.getNome();
        this.numeroUsuario = "+" + numeroUsuario;
        this.uuidUnicoUsuario = pacienteMR.getId();
        this.pacienteMR = pacienteMR;
    }

    @SneakyThrows
    @Override
    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {
        String mensagemUsuario = null;
        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();


        if (!jidNumeroUsuario.equals(numeroUsuario)) {
            return;
        }

        if (info.message().content() instanceof TextMessage textMessage) {
            mensagemUsuario = textMessage.text();
        }

        if (!(info.message().content() instanceof TextMessage textMessage)) {
            if (mensagemUsuario == null && jidNumeroUsuario.equals(numeroUsuario)) {
                whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("NÃO ACEITAMOS MENSAGEM DE ÁUDIO, FOTOS, VÍDEOS OU FIGURINHAS COMO OPÇÃO.%n%n" +
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

                if (!uuidUnicoUsuarioSet.contains(uuidUnicoUsuario)) {
                    uuidUnicoUsuarioSet.add(uuidUnicoUsuario);
                    reabrirConecxao();
                    executarPersistencia(numeroUsuario , motivo , pacienteMR);
                }
                return;
            }
            motivoDesistencia = false;
        }


        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao") &&
                !mensagemUsuario.equalsIgnoreCase("Não")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("Por favor digite uma das opções:%n%n" +
                    "(sim) caso tenha interesse na consulta.%n%n OU %n%n" +
                    "(não) para caso desistência da consulta."));
        }


        if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario),String.format("Está marcado, á sua consulta será no dia (%S).%n%n" +
                    "por favor pegue seu comprovante de agendamento com antecedência.", pacienteMR.getData()));
            whatsapp.removeListener(this);


            if (!uuidUnicoUsuarioSet.contains(uuidUnicoUsuario)) {
                uuidUnicoUsuarioSet.add(uuidUnicoUsuario);
                reabrirConecxao();
                executarPersistencia(jidNumeroUsuario , "ACEITO" , pacienteMR);
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
        try (FileOutputStream fileOutputStream = new FileOutputStream(caminho)) {
            workbook.write(new FileOutputStream(caminho));
            workbook.close();
        }

    }

    public static void reabrirConecxao() throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(new File(caminho))) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheetAt(0);
            System.out.println("CONEXÃO ABERTA");

        }
    }


    private static CompletableFuture<Void> persistiDados(String numeroUsuario , String mensagemUsuario ,PacienteMR pacienteMR) throws IOException {
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
            cellStyleParaTodos.setAlignment(HorizontalAlignment.LEFT);


            Cell celulaNomeComunitario = newRow.createCell(0);
            celulaNomeComunitario.setCellValue(pacienteMR.getNome());
            celulaNomeComunitario.setCellStyle(cellStyleParaTodos);

            cellStyleParaTodos.setAlignment(HorizontalAlignment.CENTER);
            Cell celulaNumero = newRow.createCell(1);
            celulaNumero.setCellValue("9"+numeroUsuario.substring(5));
            celulaNumero.setCellStyle(cellStyleParaTodos);


            Cell celulaBairro = newRow.createCell(2);
            celulaBairro.setCellValue(pacienteMR.getBairro().toString());
            celulaBairro.setCellStyle(cellStyleParaTodos);



            Cell celulaProcedimento= newRow.createCell(3);
            celulaProcedimento.setCellValue(pacienteMR.getConsulta().toString());
            celulaProcedimento.setCellStyle(cellStyleParaTodos);


            Cell celulaDataConsulta = newRow.createCell(4);
            celulaDataConsulta.setCellValue(pacienteMR.getData());
            celulaDataConsulta.setCellStyle(cellStyleParaTodos);



            Cell cell5 = newRow.createCell(5);
            CellStyle cellStyleCell5 = workbook.createCellStyle();
            cellStyleCell5.setBorderBottom(BorderStyle.THIN);
            cellStyleCell5.setBorderRight(BorderStyle.THIN);
            cellStyleCell5.setBorderLeft(BorderStyle.THIN);
            cellStyleCell5.setBorderTop(BorderStyle.THIN);
            Font font = workbook.createFont();
            if (!mensagemUsuario.equalsIgnoreCase("ACEITO")) {
                cellStyleCell5.setFillBackgroundColor(IndexedColors.RED1.getIndex());
                cellStyleCell5.setFillPattern(FillPatternType.DIAMONDS);
                cellStyleCell5.setAlignment(HorizontalAlignment.CENTER);
                font.setColor(IndexedColors.WHITE.getIndex());
                font.setFontName("Aptos Narrow");
                cellStyleCell5.setFont(font);
                cell5.setCellStyle(cellStyleCell5);

            }else {
                cellStyleCell5.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
                cellStyleCell5.setFillPattern(FillPatternType.DIAMONDS);
                cellStyleCell5.setAlignment(HorizontalAlignment.CENTER);
                font.setColor(IndexedColors.BLACK.getIndex());
                font.setFontName("Aptos Narrow");
                cellStyleCell5.setFont(font);
                cell5.setCellStyle(cellStyleCell5);
            }
            cell5.setCellValue(mensagemUsuario);



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

    public static void executarPersistencia(String numeroUsuario , String mensagem , PacienteMR pacienteMR) {
        queue.add(() -> {
            try {
                persistiDados(numeroUsuario , mensagem , pacienteMR).get();
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



