package com.github.dio.mensageria.documentacao.openapi.model.responserepresatation;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The type Http status 404 open api represetation.
 */
@Schema(name = "Problema")
public class HttpStatus404OpenAPIRepresetation {


    @Schema(example = "404")
    private int codigo;

    @Schema(example = "Entidade não encontrada")
    private String descricao;


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
     * Gets descricao.
     *
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Sets descricao.
     *
     * @param descricao the descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
