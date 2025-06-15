package com.github.dio.mensageria.infraestrutura.assembler;

import com.github.dio.mensageria.model.FiltroPaciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A classe responsável para montar o filtro dados os parãmetros passado no seu método static.
 * @author diogenesssantos
 */
@Component
public class AssembleFiltro {

    private static final Logger log = LoggerFactory.getLogger(AssembleFiltro.class);

    /**
     * Criando filtro filtro paciente.
     *
     * @param nome               o nome
     * @param bairro             o bairro
     * @param dataMarcacaoIncial a data marcacao inicial
     * @param dataMarcacaoFinal  a data marcacao final
     * @param consulta           o tipo de  consulta
     * @param motivo             o motivo representado por:
     *                           AGUARDANDO, NÃO_POSSUI_WHATSAPP,
     *                           NÃO_RESPONDIDO, ACEITO, NÃO_ACEITO COM MOTIVO PERSONALZIADO
     * @return the filtro paciente
     */
    public static FiltroPaciente criarFiltro(String nome, String bairro, String dataMarcacaoIncial, String dataMarcacaoFinal, String consulta, String motivo) {
        if (nome == null && bairro == null && dataMarcacaoIncial == null && dataMarcacaoFinal == null && consulta == null && motivo == null) {
            log.warn("ENTROU NA CONDIÇÃO NULL NO FILTRO DE PESQUISA");
            return null;
        } else {
            LocalDateTime dataInicialFormatada = null;
            LocalDateTime dataFinalFormatada = null;
            if (dataMarcacaoIncial != null && !dataMarcacaoIncial.isBlank()) {
                dataInicialFormatada = formatarData(dataMarcacaoIncial);
            }

            if (dataMarcacaoFinal != null && !dataMarcacaoFinal.isBlank()) {
                dataFinalFormatada = formatarData(dataMarcacaoFinal);
            }

            return new FiltroPaciente(nome, bairro, dataInicialFormatada, dataFinalFormatada, consulta, motivo);
        }
    }


    private static LocalDateTime formatarData(String data) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataFormatada = LocalDate.parse(data, formatador);
        return dataFormatada.atStartOfDay();
    }
}
