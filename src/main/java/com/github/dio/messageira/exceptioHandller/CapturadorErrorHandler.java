package com.github.dio.messageira.exceptioHandller;

import it.auties.whatsapp.implementation.SocketListener;
import it.auties.whatsapp.implementation.SocketSession;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.ChatMessageInfoSpec;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


@Component
@RegisterListener
public class CapturadorErrorHandler  implements SocketListener {


    @Override
    public void onOpen(SocketSession session) {

    }

    @Override
    public void onMessage(byte[] message, int length) {
            // Decodifique a mensagem, extraia o timestamp, etc.
            long mensagemTimestamp = extrairTimestamp(message);
            long agora = System.currentTimeMillis();

            // Exemplo: ignora mensagens com mais de 10 segundos de atraso
            if (Math.abs(agora - mensagemTimestamp) > 10000) {
                System.out.println("Ignorando mensagem offline/histórica");
                return;
            }

        ChatMessageInfoSpec.decode(message);

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onError(Throwable throwable) {

        System.err.println("Erro capturado: " + throwable.getMessage());
        throwable.printStackTrace();

        if (throwable instanceof NullPointerException) {
            System.err.println("Tratamento especial para NullPointerException.");

        }
    }




    private long extrairTimestamp(byte[] message) {
        try {
            // Supondo que os primeiros 8 bytes sejam o timestamp em formato long.
            ByteBuffer buffer = ByteBuffer.wrap(message);

            // Configure a ordem dos bytes conforme o protocolo (BIG_ENDIAN ou LITTLE_ENDIAN)
            buffer.order(ByteOrder.BIG_ENDIAN);

            return buffer.getLong();
        } catch (Exception e) {
            // Se houver algum problema, retorne o horário atual para evitar erros.
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

}
