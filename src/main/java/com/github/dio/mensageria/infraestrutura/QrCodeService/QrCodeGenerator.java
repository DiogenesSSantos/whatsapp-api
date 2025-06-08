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
 * @author diogenesssantos
 *
 * A classe responsável para gerar o QrCode em image, aonde front-end mostra para o usuario authenticar o whatsapp mobile.
 */
@Service
public class QrCodeGenerator {

    /**
     * Generate qr code image buffered image.
     *
     * @param text the text
     * @return the buffered image
     * @throws Exception the exception
     */
    public BufferedImage generateQrCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
