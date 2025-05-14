package com.github.dio.messageira.controller;

import com.github.dio.messageira.infraestruct.QrCodeService.QrCodeGenerator;
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
@RequestMapping({"/api/conexao"})
public class QrCodeController {
    @Autowired
    private QrCodeGenerator qrCodeGenerator;
    @Autowired
    WhatsappService whatsappService;

    @GetMapping(
            value = {"/qrcode"},
            produces = {"image/png"}
    )
    public ResponseEntity<InputStreamResource> getQrCodeImage() throws Exception {
        String qrCodeServiceWhatsapp = this.whatsappService.getQrCode();
        BufferedImage qrCodeImage = this.qrCodeGenerator.generateQrCodeImage(qrCodeServiceWhatsapp);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", os);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(inputStream));
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
