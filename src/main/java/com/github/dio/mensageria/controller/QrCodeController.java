package com.github.dio.mensageria.controller;

import com.github.dio.mensageria.documentacao.openapi.model.QrCodeDocumentationOpenAPI;
import com.github.dio.mensageria.infraestrutura.QrCodeService.QrCodeGenerator;
import com.github.dio.mensageria.service.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * End-poins do {@link QrCodeController}
 * @author diogenesssantos
 *
 */

@RestController
@RequestMapping({"/api/conexao"})
public class QrCodeController extends QrCodeDocumentationOpenAPI {
    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    /**
     * Injetamos a instãncia de whatsappService para pegar o valor String do QRcode gerado pela api.
     */
    @Autowired
    WhatsappService whatsappService;

    @GetMapping(
            value = {"/qrcode"},
            produces = {"image/png"}
    )
    public ResponseEntity<InputStreamResource> getQrCodeImage() throws Exception {
        String qrCodeServiceWhatsapp = this.whatsappService.getQrCode();
        BufferedImage qrCodeImage = this.qrCodeGenerator.generateQrCodeImage(qrCodeServiceWhatsapp);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", byteArrayOutputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping({"/qrcode/response"})
    public ResponseEntity<String> receberQrcode(@RequestBody QrCodeRequest qrCodeRequest) {
        String qrCodeData = qrCodeRequest.getQrCodeData();
        System.out.println("QR Code resposta recebido: " + qrCodeData);
        return new ResponseEntity("QR code resposta recebido com sucesso", HttpStatus.OK);
    }
}



class QrCodeRequest {
    private String qrCodeData;


    public String getQrCodeData() {
        return this.qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }
}
