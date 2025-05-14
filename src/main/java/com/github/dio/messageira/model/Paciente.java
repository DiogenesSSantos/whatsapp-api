package com.github.dio.messageira.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SortComparator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


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

    public Paciente() {
    }

    public Paciente(String nome, String numero, String bairro, String consulta, String dataConsulta, String motivo) {
        this.nome = nome;
        this.numero = numero;
        this.bairro = bairro;
        this.consulta = consulta;
        this.dataConsulta = dataConsulta;
        this.motivo = motivo;
    }

    public Long getId() {
        return this.id;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return this.bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getConsulta() {
        return this.consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getDataConsulta() {
        return this.dataConsulta;
    }

    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int compareTo(Paciente o) {
        return this.getNome().compareTo(o.getNome());
    }
}
