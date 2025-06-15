package com.github.dio.mensageria.documentacao.openapi.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

/**
 * A classe aonde permite converter o nosso Qrcode recebido da nossa instância do
 * {@link com.github.dio.mensageria.service.WhatsappService}
 * em uma image QrCode e posteriormente ser authenticado pelo WhatsApp.
 * @hidden
 * @author diogenesssantos
 */
@Tag(name = "QrCodeController", description = "Endpoints para gerenciar a operação de QR Code para conexão com o WhatsApp")
public abstract class QrCodeDocumentationOpenAPI {

    /**
     * O método faz a conversão usando a biblioteca zxing
     * consulte: <a href="https://zxing.github.io/zxing/apidocs/">zxing</a>
     *
     * @return qr code image em tela para ser capturado pelo aplicativo do WhatsApp.
     */
    @Operation(
            summary = "Obter QR Code",
            description = "Gera e retorna a imagem do QR Code no formato PNG, utilizada para efetivar a conexão com o WhatsApp."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "QR Code gerado com sucesso",
                    content = @Content(
                            mediaType = "image/png",
                            schema = @Schema(implementation = InputStreamResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao gerar o QR Code",
                    content = @Content
            )
    })
    public abstract ResponseEntity<InputStreamResource> getQrCodeImage()throws Exception;

    /**
     * Recebe e processa os dados do QR code enviados pelo cliente, registrando a resposta para possibilitar um fluxo de
     * reconexão.
     *
     * @param qrCodeRequest a nossa classe representacional @see {@link QrCodeRequest}.
     * @return the response entity
     */
    @Operation(
            summary = "Receber resposta do QR Code",
            description = "Recebe e processa os dados do QR Code enviados pelo cliente, registrando a resposta para possibilitar um fluxo de reconexão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR Code resposta recebido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados pelo cliente", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a resposta do QR Code", content = @Content)
    })
    public abstract ResponseEntity<String> receberQrcode(
            @RequestBody(
                    description = "JSON contendo os dados do QR Code enviados pelo cliente",
                    required = true,
                    content = @Content(schema = @Schema(implementation = QrCodeRequest.class))
            )
            QrCodeRequest qrCodeRequest
    );

    /**
     * Classe representacional para receber a resposta qrCode do front-end.
     * @author diogenes
     */
    public static class QrCodeRequest {
        @Schema(
                description = "Conteúdo da resposta do QR Code enviado pelo cliente",
                example = "exemplo-de-dado-qr-code"
        )
        private String qrCodeData;

        public String getQrCodeData() {
            return qrCodeData;
        }

        public void setQrCodeData(String qrCodeData) {
            this.qrCodeData = qrCodeData;
        }
    }
}
