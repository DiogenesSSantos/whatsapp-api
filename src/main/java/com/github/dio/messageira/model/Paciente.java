package com.github.dio.messageira.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SortComparator;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;



@AllArgsConstructor
@Entity
@Data
@Table (name = "tb_paciente")
@EqualsAndHashCode (onlyExplicitlyIncluded = true)
public class Paciente implements Comparable<Paciente> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , length = 36)
    private String codigo;


    private String nome;
    private String numero;
    private String bairro;

    @Column(name = "tipo_consulta")
    private String consulta;
    private String dataConsulta;
    private String dataMarcacao = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public int compareTo(Paciente o) {
        return this.getNome().compareTo(o.getNome());
    }
}
