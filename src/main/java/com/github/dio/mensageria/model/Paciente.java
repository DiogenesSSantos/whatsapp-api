package com.github.dio.mensageria.model;


import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


/**
 * The type Paciente.
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
     * Instantiates a new Paciente.
     */
    public Paciente() {
    }

    /**
     * Instantiates a new Paciente.
     *
     * @param nome         the nome
     * @param numero       the numero
     * @param bairro       the bairro
     * @param consulta     the consulta
     * @param dataConsulta the data consulta
     * @param motivo       the motivo
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
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Gets codigo.
     *
     * @return the codigo
     */
    public String getCodigo() {
        return this.codigo;
    }

    /**
     * Sets codigo.
     *
     * @param codigo the codigo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
     * Sets nome.
     *
     * @param nome the nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Gets numero.
     *
     * @return the numero
     */
    public String getNumero() {
        return this.numero;
    }

    /**
     * Sets numero.
     *
     * @param numero the numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
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
     * Sets bairro.
     *
     * @param bairro the bairro
     */
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    /**
     * Gets consulta.
     *
     * @return the consulta
     */
    public String getConsulta() {
        return this.consulta;
    }

    /**
     * Sets consulta.
     *
     * @param consulta the consulta
     */
    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    /**
     * Gets data consulta.
     *
     * @return the data consulta
     */
    public String getDataConsulta() {
        return this.dataConsulta;
    }

    /**
     * Sets data consulta.
     *
     * @param dataConsulta the data consulta
     */
    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    /**
     * Gets motivo.
     *
     * @return the motivo
     */
    public String getMotivo() {
        return this.motivo;
    }

    /**
     * Sets motivo.
     *
     * @param motivo the motivo
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int compareTo(Paciente o) {
        return this.getNome().compareTo(o.getNome());
    }
}
