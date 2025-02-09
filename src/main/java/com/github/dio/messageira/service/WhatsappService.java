package com.github.dio.messageira.service;


import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.listener.ListenerNovaMensagem;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.jid.Jid;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class WhatsappService {

    private static CompletableFuture<Whatsapp> whatsappFuture;
    private String qrCode;
    private Boolean isDisconnecting = true;

    @PostConstruct
    public void init() {
        whatsappFuture = new CompletableFuture<>();


        System.out.println("AGORA VOU EXECUTAR A MONTAGEM DO WHASTSAPP");
        Whatsapp.webBuilder()
                .lastConnection()
                .unregistered(qrCode -> {
                    System.out.println("QRCodeRecebido");
                    this.qrCode = qrCode;

                })
                .addLoggedInListener(api -> {
                    whatsappFuture.complete(api);
                    System.out.printf("conectado: %s%n", api.store().privacySettings());
                    isDisconnecting = false;

                })
                .addDisconnectedListener(reason -> {
                    whatsappFuture = new CompletableFuture<>();
                    System.out.printf("disconectado: %s%n", reason);
                })
                .addNewChatMessageListener(message -> System.out.printf("New message: %s%n", message.toJson()))
                .connect()
                .thenRun(() -> System.out.println("Conectado ao WhatsApp Web!"))
                .exceptionally(ex -> {
                    System.err.println("Erro ao conectar ao WhatsApp: " + ex.getMessage());
                    ex.printStackTrace();
                    whatsappFuture.completeExceptionally(ex);
                    return null;
                });


    }


    public void enviarMensagem(PacienteMR paciente) throws InterruptedException {
        for (int i = 0; i < paciente.getNumeros().size(); i++) {
            enviandoMensagemTexto(paciente, "55" + paciente.getNumeros().get(i));
            Thread.sleep(10000L);
        }


    }


    public void enviarMensagemLista(List<PacienteMR> pacienteMRList) {
        pacienteMRList.forEach(pacienteMR -> {
            try {
                enviarMensagem(pacienteMR);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private static void enviandoMensagemTexto(PacienteMR pacienteMR, String numero) {

        whatsappFuture.thenAccept(whatsapp -> {
            whatsapp.addListener(new ListenerNovaMensagem(whatsapp, pacienteMR, numero));

            try {

                if (!whatsapp.isConnected()) {
                    System.err.println("O WhatsApp não está conectado.");
                    return;
                }
                System.out.println("Enviando mensagem para: " + numero);
                var contactJid = Jid.of(numero);

                String mensagem1 = String.format(
                        "Bom dia! Somos da SECRETARIA DE SAÚDE DE VITÓRIA DE SANTO ANTÃO. Venho, por meio desta mensagem, informar sobre um comprovante de agendamento para:%n%n" +
                                "Paciente: %S.%n%n" +
                                "Consulta: %S.%n%n" +
                                "Data da Consulta: %S.%n%n" +
                                "Por favor, pegue este comprovante de agendamento com antecedência, no horário entre 08:00 e 14:00, na Secretaria de Saúde, setor de Regulação e Marcações.%n%n" +
                                "Me confirme digitando (sim), caso possua interesse.%n%n" +
                                "Caso não tenha mais interesse, digite (não) e esclareça o motivo da desistência.%n%n" +
                                "Em último caso, se não conhecer o paciente, apenas ignore esta mensagem.%n%n" +
                                "Agradeço a compreensão."
                        , pacienteMR.getNome(), pacienteMR.getConsulta(), pacienteMR.getData(), pacienteMR.getData()
                );
                whatsapp.sendMessage(contactJid, mensagem1).thenRun(() -> {
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

    public void conectar() {
            init();
    }

    public void desconectar() {
        if (isDisconnecting) {
            System.out.println("Desconexão já está em andamento.");
            return;
        }

        try {
           if (whatsappFuture != null && whatsappFuture.get() != null){
               isDisconnecting = true;
               whatsappFuture.get().logout().thenAccept(unused -> {
                   whatsappFuture = new CompletableFuture<>();
                   System.out.println("API DESCONECTADA");
               }).exceptionally(throwable -> {
                    System.out.println("ERRO NA API" + throwable);
                   isDisconnecting = false;
                   return null;
               });

           }

        }catch (Exception e){
            new RuntimeException("ALGUMA ERRO NA APLICAÇÃO");
        }
    }

    public String getQrCode() {
        return qrCode;
    }
}
