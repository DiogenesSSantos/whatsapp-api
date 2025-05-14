package com.github.dio.messageira.infraestruct.assembler;

import com.github.dio.messageira.model.Filtro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AssembleFiltro {
    private static final Logger log = LoggerFactory.getLogger(AssembleFiltro.class);

    public static Filtro criandoFiltro(String nome, String bairro, String dataMarcacaoIncial, String dataMarcacaoFinal, String consulta, String motivo) {
        if (nome == null && bairro == null && dataMarcacaoIncial == null && dataMarcacaoFinal == null && consulta == null && motivo == null) {
            log.warn("ENTROU NA CONDIÇÃO NULL DO ASSEMBLER");
            return null;
        } else {
            LocalDateTime dataInicialFormatada = null;
            LocalDateTime dataFinalFormatada = null;
            if (dataMarcacaoIncial != null && !dataMarcacaoIncial.isBlank()) {
                dataInicialFormatada = formataData(dataMarcacaoIncial);
            }

            if (dataMarcacaoFinal != null && !dataMarcacaoFinal.isBlank()) {
                dataFinalFormatada = formataData(dataMarcacaoFinal);
            }

            return new Filtro(nome, bairro, dataInicialFormatada, dataFinalFormatada, consulta, motivo);
        }
    }

    private static LocalDateTime formataData(String data) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataFormatada = LocalDate.parse(data, formatador);
        return dataFormatada.atStartOfDay();
    }
}
