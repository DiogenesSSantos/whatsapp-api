package com.github.dio.messageira.core.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Problema-500")
public class HttpStatus500OpenAPIRepresetation {


    @Schema(example = "500")
    private int codigo;

    @Schema(example = "SERVER_INTERN_ERROR")
    private String nome;


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
