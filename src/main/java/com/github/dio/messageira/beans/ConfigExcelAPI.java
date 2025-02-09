package com.github.dio.messageira.beans;


import com.github.dio.messageira.core.PacienteConfirmadoService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Component
public class ConfigExcelAPI {
    private static String caminho =
            "C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\salvar-marcações\\marcacoes-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";


    @Bean
    private static void excelApi() throws IOException, InvalidFormatException {
        var workbook = new XSSFWorkbook("C:\\Users\\Dioge\\OneDrive\\Área de Trabalho\\arquivo-planilha-cmce\\copia-para-usar-projeto.xlsx");
        var sheet = workbook.getSheet("ESPECIALIDADES");
        try (FileOutputStream fileOutputStream = new FileOutputStream(caminho)) {
            workbook.write(new FileOutputStream(caminho));
            workbook.close();
        }

    }


}
