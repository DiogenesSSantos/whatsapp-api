package com.github.dio.mensageria.model;

import java.time.LocalDateTime;

/**
 * The type Filtro paciente.
 */
public class FiltroPaciente {
    private String nome;
    private String bairro;
    private LocalDateTime dataMarcacaoInicial;
    private LocalDateTime dataMarcacaoFinal;
    private String tipoConsulta;
    private String motivo;

    /**
     * Instantiates a new Filtro paciente.
     *
     * @param nome                the nome
     * @param bairro              the bairro
     * @param dataMarcacaoInicial the data marcacao inicial
     * @param dataMarcacaoFinal   the data marcacao final
     * @param tipoConsulta        the tipo consulta
     * @param motivo              the motivo
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
     * @return the nome
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Gets bairro.
     *
     * @return the bairro
     */
    public String getBairro() {
        return this.bairro;
    }

    /**
     * Gets data marcacao inicial.
     *
     * @return the data marcacao inicial
     */
    public LocalDateTime getDataMarcacaoInicial() {
        return this.dataMarcacaoInicial;
    }

    /**
     * Gets data marcacao final.
     *
     * @return the data marcacao final
     */
    public LocalDateTime getDataMarcacaoFinal() {
        return this.dataMarcacaoFinal;
    }

    /**
     * Gets tipo consulta.
     *
     * @return the tipo consulta
     */
    public String getTipoConsulta() {
        return this.tipoConsulta;
    }

    /**
     * Gets motivo.
     *
     * @return the motivo
     */
    public String getMotivo() {
        return this.motivo;
    }
}

