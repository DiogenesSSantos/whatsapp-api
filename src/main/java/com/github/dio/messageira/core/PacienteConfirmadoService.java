package com.github.dio.messageira.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dio.messageira.core.PacienteCoreModel.PacienteCoreModelVersao2;
import com.github.dio.messageira.core.webSockets.PacienteWebSockets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@EnableScheduling
public class PacienteConfirmadoService {
    private static final BlockingQueue<PacienteCoreModelVersao2> FILA =  new LinkedBlockingQueue<>();
    private static List<PacienteCoreModelVersao2> pacienteCoreModels = new ArrayList<>();




    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService executadorWebSocket = Executors.newScheduledThreadPool(1);
    private final PacienteWebSockets pacienteWebSockets;

    public PacienteConfirmadoService(PacienteWebSockets pacienteWebSockets) {
        this.pacienteWebSockets = pacienteWebSockets;
    }


    private Callable<Void> copiadorDePlanilha () {
        return () -> {
            String arquivoDestino = "C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\salvar-marcações\\marcacoes-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx"; // Caminho do arquivo original
            String copiaArquivo = "C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\pastaCopia-planilhas\\copia-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";     // Caminho do arquivo de cópia

            try (FileInputStream fis = new FileInputStream(arquivoDestino);
                 Workbook workbook = new XSSFWorkbook(fis);
                 FileOutputStream fos = new FileOutputStream(copiaArquivo)) {

                workbook.write(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        };
    }

    private Callable<Void> producao() {
        return () -> {
            try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\pastaCopia-planilhas\\copia-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx"))) {
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);

                int startRow = 6;

                while (startRow <= sheet.getLastRowNum()) {
                    Row row = sheet.getRow(startRow);

                    if (row != null) {
                        boolean linhaVazia = true;
                        for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                            Cell cell = row.getCell(cn);

                            if (cell != null && cell.getCellType() != CellType.BLANK) {
                                linhaVazia = false;
                                break;
                            }
                        }

                        if (linhaVazia){
                            startRow++;
                            continue;
                        }

                        String nome = row.getCell(0).toString();
                        String numero = row.getCell(1).toString();
                        String bairro = row.getCell(2).toString();
                        String consulta = row.getCell(3).toString();
                        String data = row.getCell(4).toString();
                        String motivo = row.getCell(5).toString();

                        var pacienteVersao2 = new PacienteCoreModelVersao2(nome , numero,bairro,consulta,data,motivo);

                        FILA.add(pacienteVersao2);

                    }

                    startRow++;
                }

                FILA.put(new PacienteCoreModelVersao2("-1", "FIM" ,"" , "" , "" , ""));

            } catch (Exception e) {
                System.out.println("ERRO: " + e);
            }
            return null;
        };
    }


    private Callable<Void> consumidor() {
        return () -> {
            try {
                pacienteCoreModels.clear();
                while (true) {
                    PacienteCoreModelVersao2 paciente = FILA.take();
                    if (paciente.getNome().equals("-1") && "FIM".equals(paciente.getNumero())) {
                        break;
                    }
                    pacienteCoreModels.add(paciente);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrompida: " + e);
            }
            return null;
        };

    }


    public List<PacienteCoreModelVersao2> executandoProducaoAndCosumidor() {
        try {
            List<Future<Void>> futureList = executorService.invokeAll(List.of( copiadorDePlanilha(), producao() , consumidor()));

            for (Future future : futureList) {
                future.get();
            }
        }catch (Exception e) {
            System.out.println("ERRO: " + e);
        }
        return pacienteCoreModels;
    }

    @Scheduled(fixedRate = 1000)
    public void executandoUpdate() {
        Runnable atualizando = () -> {
            List<PacienteCoreModelVersao2> pacienteCoreModels1 = executandoProducaoAndCosumidor();
            String JsonFormatado = converterParaJson(pacienteCoreModels1);
            try {
                pacienteWebSockets.enviarMessagemParaOsClientes(JsonFormatado);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        executadorWebSocket.schedule(atualizando , 5 , TimeUnit.SECONDS);
    }


    /**
     * @AUTHOR Diogenes_Santos
     * Método criado com auxilio da biblioteca ObjectMapper para manipulação de dados, Objetivo e conversão para JSON.
     *
     * @param pacienteCoreModelsList recebemos a lista ja produzida e consumida para converter para um objeto
     *        JSON para o front-end Manipular.
     *
     * @return String ja em formato JSON nesse exemplo
     * [
     *  numero : "?",
     *  nome : "NOME DO PACIENTE",
     *  numeroTelefoneQueConfirmou : "9 9999-9999" (sem o espaço no primerio digíto)
     *  procedimento : "TIPO_CONSULTA
     *  MOTIVO : "SENDO STATUS ACEITO OU QUALQUER OUTRO COM JUSTIFICATIVA DO PACIENTE"
     * ]
     */
    private String converterParaJson(List<PacienteCoreModelVersao2> pacienteCoreModelsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(pacienteCoreModelsList);
        } catch (Exception e) {
            return "[]";
        }
    }

}
