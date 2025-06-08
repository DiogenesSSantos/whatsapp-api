package com.github.dio.mensageria.model.modeloRepresentacional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * The type Paciente mr.
 */
@Schema(name = "PacienteMR", description = "Atributos")
@Data
@Getter
@Setter
public class PacienteMR {
    @Schema(
            description = "Gera uma id aleatório para usuário",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            hidden = true
    )
    private UUID id = UUID.randomUUID();
    @Schema(
            description = "Nome do paciente",
            example = "Diogenes",
            required = true
    )
    private String nome;
    @Schema(
            description = "Lista de números de telefone do paciente sem precisar passar o digito 9 antes do numero",
            example = "[\"digite aqui apenas numero exemplo->8188889999\", \"digite aqui apenas numero exemplo->8188889999\"]",
            required = true
    )
    private List<String> numeros;
    @Schema(
            description = "Bairro do paciente",
            example = ""
    )
    private String bairro;
    @Schema(
            description = "Descrição da consulta do paciente",
            example = ""
    )
    private String consulta;
    @Schema(
            description = "Data da consulta do paciente",
            example = "01/01/2025"
    )
    private String data;

    /**
     * Gets id.
     *
     * @return the id
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(UUID id) {
        this.id = id;
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
     * Gets numeros.
     *
     * @return the numeros
     */
    public List<String> getNumeros() {
        return this.numeros;
    }

    /**
     * Sets numeros.
     *
     * @param numeros the numeros
     */
    public void setNumeros(List<String> numeros) {
        this.numeros = numeros;
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
     * Gets data.
     *
     * @return the data
     */
    public String getData() {
        return this.data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(String data) {
        this.data = data;
    }
}
