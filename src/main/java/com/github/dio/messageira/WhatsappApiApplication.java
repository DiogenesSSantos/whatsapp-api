package com.github.dio.messageira;

import it.auties.whatsapp.api.PairingCodeHandler;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.contact.Contact;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.mobile.VerificationCodeMethod;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class WhatsappApiApplication {

    private static CompletableFuture<Whatsapp> whatsappFuture;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(WhatsappApiApplication.class, args);
//        reconnectWhatsappInstance();
//        var contato = new Contact(Jid.of(558186189045l));
//        whatsappFuture.get().sendMessage(contato , "Procure a recepção e mostre a mensagem a qualquer recepcionista que vai ser lhe entregue a marcação.)");
//        //whatsappFuture.get().logout();
    }


//    private static void requestWhatsappInstanceQrCode() {
//        whatsappFuture = new CompletableFuture<>();
//
//        Whatsapp.webBuilder()
//                .lastConnection()
//                .unregistered(QrHandler.toTerminal())
//                .addLoggedInListener(api -> {
//                    whatsappFuture.complete(api);
//                    System.out.printf("Connected: %s%n", api.store().privacySettings());
//                })
//                .addDisconnectedListener(reason -> {
//                    whatsappFuture = new CompletableFuture<>();
//                    System.out.printf("Disconnected: %s%n", reason);
//                })
//                .addNewChatMessageListener(message -> System.out.printf("New message: %s%n", message.toJson()))
//                .connect()
//                .thenRun(() -> System.out.println("Conectado ao WhatsApp Web!")).exceptionally(ex -> {
//                    System.err.println("Erro ao conectar ao WhatsApp: " + ex.getMessage());
//                    ex.printStackTrace();
//                    whatsappFuture.completeExceptionally(ex);
//                    return null;
//
//                });
//    }
//
//
//    public static void reconnectWhatsappInstance() {
//        try {
////            if (whatsappFuture.get() != null) {
////                System.out.println("Desconectando a sessão anterior...");
////                whatsappFuture.get().disconnect();
////                whatsappFuture = null;
////            }
//            System.out.println("Tentando reconectar...");
//            requestWhatsappInstanceQrCode();
//        } catch (Exception e) {
//            System.err.println("Erro ao reconectar o WhatsApp: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public static void sendMessageToNumber(String phoneNumber, String message) {
//        whatsappFuture.thenAccept(whatsapp -> {
//            try {
//                if (!whatsapp.isConnected()) {
//                    System.err.println("O WhatsApp não está conectado.");
//                    return;
//                }
//                System.out.println("Enviando mensagem para: " + phoneNumber);
//                var contactJid = Jid.of(phoneNumber);
//                whatsapp.sendMessage(contactJid, message).thenRun(() -> {
//                    System.out.println("Mensagem enviada para: " + phoneNumber);
//                }).exceptionally(ex -> {
//                    System.err.println("Erro ao enviar mensagem: " + ex.getMessage());
//                    ex.printStackTrace();
//                    return null;
//                });
//            } catch (Exception e) {
//                System.err.println("Erro ao enviar mensagem: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).exceptionally(ex -> {
//            System.err.println("Falha ao obter a instância do WhatsApp: " + ex.getMessage());
//            return null;
//        });
//    }

}