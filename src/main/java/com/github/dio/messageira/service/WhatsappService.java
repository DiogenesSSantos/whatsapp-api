package com.github.dio.messageira.service;

import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.model.Paciente;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.button.base.*;
import it.auties.whatsapp.model.info.NativeFlowInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.button.ButtonsMessageBuilder;
import it.auties.whatsapp.model.message.button.ButtonsMessageHeader;
import it.auties.whatsapp.model.message.button.InteractiveMessage;
import it.auties.whatsapp.model.message.button.InteractiveMessageBuilder;
import it.auties.whatsapp.model.message.model.ButtonMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class WhatsappService {

    private static CompletableFuture<Whatsapp> whatsappFuture;


    /**
     * INJEÇÃO DA INSTANCIA DO WHATSAPP PARA USO CONTINUO NO SERVIÇO, LOGIN FEITO POR QRCode!
     * FUTURAMENTE INJEÇÕES COM CODIGO DE SMS
     */
    @PostConstruct
    public void init() {
        whatsappFuture = new CompletableFuture<>();

        Whatsapp.webBuilder()
                .lastConnection()
                .unregistered(QrHandler.toTerminal())
                .addLoggedInListener(api -> {
                    whatsappFuture.complete(api);
                    System.out.printf("Connected: %s%n", api.store().privacySettings());
                })
                .addDisconnectedListener(reason -> {
                    whatsappFuture = new CompletableFuture<>();
                    System.out.printf("Disconnected: %s%n", reason);
                })
                .addNewChatMessageListener(message -> System.out.printf("New message: %s%n", message.toJson()))
                .connect()
                .thenRun(() -> System.out.println("Conectado ao WhatsApp Web!")).exceptionally(ex -> {
                    System.err.println("Erro ao conectar ao WhatsApp: " + ex.getMessage());
                    ex.printStackTrace();
                    whatsappFuture.completeExceptionally(ex);
                    return null;

                });
    }

    //TODO Futuramente buscar injeção de uma instancia do whatsapp pelo CODIGO SMS.



    public void enviarMensagem(String numero, String mensagem) {
        enviandoMensagemTexto(numero, mensagem);
    }


    public void enviarMensagemLista(List<String> numeros, String mensagem) {

        numeros.forEach(s ->  {
            try {
                Thread.sleep(3000);
                enviarMensagem(s , mensagem);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void enviarMensagemComBotao(String numero, String mensagem) {
        enviandoMensagemComBotao(numero);

    }


    private static void enviandoMensagemTexto(String numero, String mensagem) {
        whatsappFuture.thenAccept(whatsapp -> {
            try {
                if (!whatsapp.isConnected()) {
                    System.err.println("O WhatsApp não está conectado.");
                    return;
                }
                System.out.println("Enviando mensagem para: " + numero);
                var contactJid = Jid.of(numero);
                whatsapp.sendMessage(contactJid, mensagem).thenRun(() -> {
                    System.out.println("Mensagem enviada para: " + numero);
                }).exceptionally(ex -> {
                    System.err.println("Erro ao enviar mensagem: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
            } catch (Exception e) {
                System.err.println("Erro ao enviar mensagem: " + e.getMessage());
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            System.err.println("Falha ao obter a instância do WhatsApp: " + ex.getMessage());
            return null;
        });
    }


    private static void enviandoMensagemComBotao(String numero) {
        whatsappFuture.thenAccept(whatsapp -> {
            var numeroJid = Jid.of(numero);

            var butaoSim = new Button("Sim",
                    Optional.of(new ButtonText("Sim")),
                    Optional.empty(),
                    ButtonBody.Type.TEXT
            );
            var butaoNao = new Button("Não",
                    Optional.of(new ButtonText("Não")),
                    Optional.empty(),
                    ButtonBody.Type.TEXT
            );

            var botao = new ButtonsMessageBuilder()
                    .body("ESCOLHA UM BOTÃO ABAIXO")
                    .headerType(ButtonsMessageHeader.Type.EMPTY)
                    .buttons(List.of(butaoSim, butaoNao))
                    .build();

            whatsapp.sendMessage(numeroJid, botao);
        }).exceptionally(ex -> {
            System.err.println("Falha ao obter a instância do WhatsApp: " + ex.getMessage());
            return null;
        });
    }

}
