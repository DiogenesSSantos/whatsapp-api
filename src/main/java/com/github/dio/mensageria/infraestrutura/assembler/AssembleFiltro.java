package com.github.dio.mensageria.infraestrutura.assembler;

import com.github.dio.mensageria.model.FiltroPaciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Assemble filtro.
 */
@Component
public class AssembleFiltro {
    private static final Logger log = LoggerFactory.getLogger(AssembleFiltro.class);

    /**
     * Criando filtro filtro paciente.
     *
     * @param nome               the nome
     * @param bairro             the bairro
     * @param dataMarcacaoIncial the data marcacao incial
     * @param dataMarcacaoFinal  the data marcacao final
     * @param consulta           the consulta
     * @param motivo             the motivo
     * @return the filtro paciente
     */
    public static FiltroPaciente criandoFiltro(String nome, String bairro, String dataMarcacaoIncial, String dataMarcacaoFinal, String consulta, String motivo) {
        if (nome == null && bairro == null && dataMarcacaoIncial == null && dataMarcacaoFinal == null && consulta == null && motivo == null) {
            log.warn("ENTROU NA CONDIÇÃO NULL NO FILTRO DE PESQUISA");
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

            return new FiltroPaciente(nome, bairro, dataInicialFormatada, dataFinalFormatada, consulta, motivo);
        }
    }

    private static LocalDateTime formataData(String data) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataFormatada = LocalDate.parse(data, formatador);
        return dataFormatada.atStartOfDay();
    }
}
