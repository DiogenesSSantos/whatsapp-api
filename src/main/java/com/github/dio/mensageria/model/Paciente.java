package com.github.dio.mensageria.model;


import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


/**
 * Classe entity, a mesma persistida no banco de dados.
 *
 * @author diogenessantos
 */
@Entity
@Table(
        name = "tb_paciente"
)
public class Paciente implements Comparable<Paciente> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            unique = true,
            length = 36
    )
    private String codigo;
    private String nome;
    private String numero;
    private String bairro;
    @Column(
            name = "tipo_consulta"
    )
    private String consulta;
    private String dataConsulta;
    @DateTimeFormat(
            pattern = "dd/MM/yyyy"
    )
    private LocalDateTime dataMarcacao = LocalDateTime.now();
    private String motivo;

    /**
     * Inicialização padrão Paciente para serialização.
     *
     * @hidden .
     */
    public Paciente() {
    }

    /**
     * Os seus parãmetros de inicialização.
     *
     * @param nome         o nome
     * @param numero       o numero
     * @param bairro       o bairro
     * @param consulta     o tipo consulta
     * @param dataConsulta a data consulta
     * @param motivo       o motivo decidido no {@link com.github.dio.mensageria.service.WhatsappService} em conjunto                     {@link com.github.dio.mensageria.listener.ListenerNovaMensagem}
     */
    public Paciente(String nome, String numero, String bairro, String consulta, String dataConsulta, String motivo) {
        this.nome = nome;
        this.numero = numero;
        this.bairro = bairro;
        this.consulta = consulta;
        this.dataConsulta = dataConsulta;
        this.motivo = motivo;
    }


    /**
     * Gets id.
     *
     * @return o id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Gets codigo.
     *
     * @return o código
     */
    public String getCodigo() {
        return this.codigo;
    }


    /**
     * Sets código.
     *
     * @param codigo o código
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
     * Sets nome.
     *
     * @param nome o nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Gets número.
     *
     * @return the número
     */
    public String getNumero() {
        return this.numero;
    }


    /**
     * Sets número.
     *
     * @param numero o número
     */
    public void setNumero(String numero) {
        this.numero = numero;
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
     * Sets bairro.
     *
     * @param bairro o bairro
     */
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }


    /**
     * Gets consulta.
     *
     * @return o tipo consulta
     */
    public String getConsulta() {
        return this.consulta;
    }


    /**
     * Sets consulta.
     *
     * @param consulta o consulta
     */
    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }


    /**
     * Gets data consulta.
     *
     * @return a data da consulta
     */
    public String getDataConsulta() {
        return this.dataConsulta;
    }


    /**
     * Sets data consulta.
     *
     * @param dataConsulta a data consulta
     */
    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }


    /**
     * Gets motivo.
     *
     * @return o motivo
     */
    public String getMotivo() {
        return this.motivo;
    }


    /**
     * Sets motivo.
     *
     * @param motivo o motivo
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int compareTo(Paciente o) {
        return this.getNome().compareTo(o.getNome());
    }
}
