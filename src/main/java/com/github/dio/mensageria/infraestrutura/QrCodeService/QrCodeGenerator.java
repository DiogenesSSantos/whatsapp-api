package com.github.dio.mensageria.infraestrutura.QrCodeService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe responsável para gerar o QrCode em image, aonde front-end mostra para o usuário autenticar o WhatsApp.
 * @author diogenesssantos
 *
 */
@Service
public class QrCodeGenerator {

    /**
     * Gera o qr code image buffered image para front-end.
     *
     * @param text String é recebido da {@link com.github.dio.mensageria.service.WhatsappService}
     * @return  buffered image.
     */
    public BufferedImage generateQrCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
