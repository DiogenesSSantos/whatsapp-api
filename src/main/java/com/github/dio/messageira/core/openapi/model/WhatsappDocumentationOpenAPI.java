package com.github.dio.messageira.core.openapi.model;

import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "ESTOU USANDO CLASSE ABSTRATA PARA DOCUMENTAR", description = "FODASSE")
public abstract class WhatsappDocumentationOpenAPI {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpStatus404OpenAPIRepresetation.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpStatus500OpenAPIRepresetation.class)))
    })
    @Operation(summary = "Vai para pagina incial", description = "Mostra o front end do projeto")
    public abstract String home();

    @Operation(summary = "Envia a lista de numeros",
            description = "Front end manda requisão para o endpoints para enviar mensagem")
    public abstract void enviarParaLista(@RequestBody(required = true, description = "CORPO JSON DA REQUISIÇÃO ") List<PacienteMR> pacienteMR);

    @Operation(summary = "Desconectar a instancia", description = "Desconecta a instancia do whatsapp, " +
            "em seguida voce deve usar o método reconectar para obter uma nova QrCode de conexão")
    public abstract void desconectarWhatsApp();

    @Operation(summary = "Reconecta whatsapp na aplicação", description = "Depois de reconecta usario deve" +
            " chamar endpoints do QrCode")
    public abstract void reconectarWhatsApp();


}
