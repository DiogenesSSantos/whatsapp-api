package com.github.dio.messageira.controller.modeloRepresentacional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(name = "Paciente modelo repressaticional", description = "Atributos")
@Data
@Getter
@Setter
public class PacienteMR {

    @Schema(description = "Gera uma id aleatório para usuário", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6" , hidden = true)
    private UUID id = UUID.randomUUID();

    @Schema(description = "Nome do paciente", example = "Diogenes", required = true)
    private String nome;

    @Schema(description = "Lista de números de telefone do paciente sem precisar passar o digito 9 antes do numero", example = "[\"digite aqui apenas numero exemplo->8188889999\", " +
            "\"digite aqui apenas numero exemplo->8188889999\"]" , required = true)
    private List<String> numeros;

    @Schema(description = "Bairro do paciente", example = "")
    private String bairro;

    @Schema(description = "Descrição da consulta do paciente", example = "")
    private String consulta;

    @Schema(description = "Data da consulta do paciente", example = "01/01/2050")
    private String data;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getNumeros() {
        return numeros;
    }

    public void setNumeros(List<String> numeros) {
        this.numeros = numeros;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
