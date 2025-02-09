package com.github.dio.messageira.core.webSockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class PacienteWebSockets extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacienteWebSockets.class);
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        LOGGER.info("WebSocket ESTABELECIDA : " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        LOGGER.info("WebSocket ENCERRADA : " + session.getId());
    }


    public void enviarMessagemParaOsClientes(String planilha) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(planilha));
                    LOGGER.info("Mensagem enviada para o ID: " + session.getId() + " - Mensagem: " + planilha);
                } catch (IOException e) {
                    LOGGER.error("Erro ao enviar mensagem para o ID: " + session.getId(), e);
                }
            } else {
                LOGGER.warn("Sessão não está aberta para o ID: " + session.getId());
            }
        }
    }


}
