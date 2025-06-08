package com.github.dio.mensageria.documentacao.openapi.model.responserepresatation;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The type Http status 500 open api represetation.
 */
@Schema(name = "Problema-500")
public class HttpStatus500OpenAPIRepresetation {


    @Schema(example = "500")
    private int codigo;

    @Schema(example = "SERVER_INTERN_ERROR")
    private String nome;


    /**
     * Gets codigo.
     *
     * @return the codigo
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Sets codigo.
     *
     * @param codigo the codigo
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Gets nome.
     *
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets nome.
     *
     * @param nome the nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
}
