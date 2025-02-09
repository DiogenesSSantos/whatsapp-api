package com.github.dio.messageira.controller;

import com.github.dio.messageira.service.QrCodeService.QrCodeGenerator;
import com.github.dio.messageira.service.WhatsappService;
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

@RestController
@RequestMapping("/api/whatsapp")
public class QrCodeController {

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @Autowired
    WhatsappService whatsappService;


    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getQrCodeImage() throws Exception {
        String qrCodeServiceWhatsapp = whatsappService.getQrCode();

        BufferedImage qrCodeImage = qrCodeGenerator.generateQrCodeImage(qrCodeServiceWhatsapp);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", os);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(inputStream));

    }

    @PostMapping("/qrcode/response")
    public ResponseEntity<String> receberQrcode(@RequestBody QrCodeRequest qrCodeRequest) {
        String qrCodeData = qrCodeRequest.getQrCodeData();
        System.out.println("QR Code resposta recebido: " + qrCodeData);
        // Adicione a lógica para processar o QR code aqui

        return new ResponseEntity<>("QR code resposta recebido com sucesso", HttpStatus.OK);
    }

}

    class QrCodeRequest {
        private String qrCodeData;

        public String getQrCodeData() {
            return qrCodeData;
        }

        public void setQrCodeData(String qrCodeData) {
            this.qrCodeData = qrCodeData;
        }
}
