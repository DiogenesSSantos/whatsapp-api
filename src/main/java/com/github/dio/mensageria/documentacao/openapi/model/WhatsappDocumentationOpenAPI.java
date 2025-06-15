package com.github.dio.mensageria.documentacao.openapi.model;

import com.github.dio.mensageria.documentacao.openapi.model.responserepresatation.HttpStatus404OpenAPIRepresetation;
import com.github.dio.mensageria.documentacao.openapi.model.responserepresatation.HttpStatus500OpenAPIRepresetation;
import com.github.dio.mensageria.model.modeloRepresentacional.PacienteMR;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


/**
 * A classe responsável pelos end-points de envio de mensagem, conexão e desconexão do whastsapp.
 * @hidden
 * @author diogenessantos
 */
@Tag(name = "WhatsappController", description = "Endpoints para manipulação da comunicação via WhatsApp")
public abstract class WhatsappDocumentationOpenAPI {

    /**
     * Recebe um corpo JSON do front-end representando
     * @param pacienteMR
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpStatus404OpenAPIRepresetation.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpStatus500OpenAPIRepresetation.class)))
    })

    @Operation(
            summary = "Envia a lista de números",
            description = "O front-end envia a requisição para este endpoint com o objetivo de enviar mensagens para a lista de pacientes."
    )
    public abstract void enviarParaLista(
            @RequestBody(required = true, description = "Corpo JSON da requisição contendo a lista de pacientes.")
            List<PacienteMR> pacienteMR
    );

    /**
     * End-point de desconexão do whatsapp veja o método desconectar()
     * da classe {@link com.github.dio.mensageria.service.WhatsappService}, possui toda regra de negocio documentada.
     */
    @Operation(
            summary = "Desconecta a instância do WhatsApp",
            description = "Desconecta a instância atual do WhatsApp e encerra a sessão ativa. Após a desconexão, utilize o endpoint de reconexão para obter um novo QR Code de acesso."
    )
    public abstract void desconectarWhatsApp();

    /**
     * End-point de reconexao do whatsapp, mantendo a primeira conexão feita pelo usuário veja o método conectar()
     * da classe {@link com.github.dio.mensageria.service.WhatsappService}, possui toda regra de negocio documentada.
     */
    @Operation(
            summary = "Reconecta o WhatsApp na aplicação",
            description = "Reinicia a sessão do WhatsApp, permitindo que o usuário obtenha um novo QR Code de conexão para restabelecer a comunicação."
    )
    public abstract void reconectarWhatsApp();

}
