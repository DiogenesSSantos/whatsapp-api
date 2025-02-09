package com.github.dio.messageira.core.webSockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class websocketConfiguracao implements WebSocketConfigurer {
    private final PacienteWebSockets pacienteWebSocketHandler;

    @Autowired
    public websocketConfiguracao(PacienteWebSockets pacienteWebSocketHandler) {
        this.pacienteWebSocketHandler = pacienteWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
       registry.addHandler(pacienteWebSocketHandler , "/paciente-updates").setAllowedOrigins("*");
    }
}
