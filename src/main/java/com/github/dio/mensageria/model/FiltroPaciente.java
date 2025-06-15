package com.github.dio.mensageria.model;

import java.time.LocalDateTime;

/**
 * Classe utilizada como fitlro do {@link com.github.dio.mensageria.repository.PacienteRepositoryImpl}
 * fazendo a busca por parâmetros passado, explicado aonde o mesmo é utilizado {@link com.github.dio.mensageria.controller.EstatisticasController}
 *
 * @author diogenesssantos
 */
public class FiltroPaciente {
    private String nome;
    private String bairro;
    private LocalDateTime dataMarcacaoInicial;
    private LocalDateTime dataMarcacaoFinal;
    private String tipoConsulta;
    private String motivo;

    /**
     * Inicialização do objeto {@link FiltroPaciente}.
     *
     * @param nome                o nome
     * @param bairro              o bairro
     * @param dataMarcacaoInicial o período de data marcação inicial
     * @param dataMarcacaoFinal   o período marcação final
     * @param tipoConsulta        o tipo consulta
     * @param motivo              o motivo corresponde aos valores AGUARDANDO, NÃO_POSSUI_WHATSAPP,
     *                            NÃO_RESPONDIDO, ACEITO, REJEITADO + MOTIVO DESCRITO
     */
    public FiltroPaciente(String nome, String bairro, LocalDateTime dataMarcacaoInicial, LocalDateTime dataMarcacaoFinal, String tipoConsulta, String motivo) {
        this.nome = nome;
        this.bairro = bairro;
        this.dataMarcacaoInicial = dataMarcacaoInicial;
        this.dataMarcacaoFinal = dataMarcacaoFinal;
        this.tipoConsulta = tipoConsulta;
        this.motivo = motivo;
    }

    /**
     * Gets nome.
     *
     * @return o nome
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Gets bairro.
     *
     * @return o bairro
     */
    public String getBairro() {
        return this.bairro;
    }

    /**
     * Gets data marcacao inicial.
     *
     * @return a data marcacao inicial
     */
    public LocalDateTime getDataMarcacaoInicial() {
        return this.dataMarcacaoInicial;
    }

    /**
     * Gets data marcacao final.
     *
     * @return a data marcacao final
     */
    public LocalDateTime getDataMarcacaoFinal() {
        return this.dataMarcacaoFinal;
    }

    /**
     * Gets tipo consulta.
     *
     * @return o tipo consulta
     */
    public String getTipoConsulta() {
        return this.tipoConsulta;
    }

    /**
     * Gets motivo.
     *
     * @return o motivo
     */
    public String getMotivo() {
        return this.motivo;
    }
}

