package com.github.dio.messageira;

import com.github.dio.messageira.core.PacienteConfirmadoService;
import com.github.dio.messageira.core.PacienteCoreModel.PacienteCoreModelVersao2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
class WhatsappApiApplicationTests {
	@Autowired
	PacienteConfirmadoService pacienteConfirmadoService;



	@Test
	void contextLoads() {
	}


	@Test
	public void executorService_Test() {
		for (int i = 0 ; i <= 10 ; i++){
			List<PacienteCoreModelVersao2> pacienteCoreModels = pacienteConfirmadoService.executandoProducaoAndCosumidor();
			System.out.println(pacienteCoreModels+"\n");
		}
		pacienteConfirmadoService.executandoUpdate();


	}


}
