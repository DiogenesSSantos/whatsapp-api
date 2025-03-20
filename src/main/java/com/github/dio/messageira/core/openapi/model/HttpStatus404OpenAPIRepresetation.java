package com.github.dio.messageira.core.openapi.model;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Problema")
public class HttpStatus404OpenAPIRepresetation {


    @Schema(example = "404")
    private int codigo;

    @Schema(example = "Entidade não encontrada")
    private String descricao;


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
