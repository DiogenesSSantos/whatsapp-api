
package com.github.dio.messageira.service;


import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.listener.ListenerNovaMensagem;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.model.jid.Jid;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class WhatsappService {


    private static CompletableFuture<Whatsapp> whatsappFuture;
    private String qrCode;
    private Boolean isDisconnecting = true;

    private Set<String> pacienteSetStringUUID = new HashSet<>();

    private PacienteRepository pacienteRepository;

    @Autowired
    public WhatsappService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }


    @PostConstruct
    public void init() {
        whatsappFuture = new CompletableFuture<>();

        Whatsapp.webBuilder()
                .lastConnection()
                .unregistered(qrCode -> {
                    System.out.println("QRCodeRecebido");
                    this.qrCode = qrCode;

                })
                .addLoggedInListener(api -> {
                    whatsappFuture.complete(api);
                    System.out.printf("conectado: %s%n" , api.store().privacySettings());
                    isDisconnecting = false;

                })
                .addDisconnectedListener(reason -> {
                    whatsappFuture = new CompletableFuture<>();
                    System.out.printf("disconectado: %s%n" , reason);
                })
                .addNewChatMessageListener(message -> System.out.printf("nova mensagem: %s%n" , message.toJson()))
                .connect()
                .thenRun(() -> System.out.println("Conectado ao WhatsApp Web!"))
                .exceptionally(ex -> {
                    System.err.println("Erro ao conectar ao WhatsApp: " + ex.getMessage());
                    ex.printStackTrace();
                    whatsappFuture.completeExceptionally(ex);
                    return null;
                });


    }


    public void conectar() {
        desconectar();
        init();
    }



    public void desconectar() {
        if (isDisconnecting) {
            System.out.println("Desconexão já está em andamento.");
            return;
        }

        try {
            if (whatsappFuture != null && whatsappFuture.get() != null) {
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

        } catch (Exception e) {
            new RuntimeException("ALGUMA ERRO NA APLICAÇÃO");
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


    public void enviarMensagem(PacienteMR paciente) throws InterruptedException {
        for (int i = 0; i < paciente.getNumeros().size(); i++) {
            enviandoMensagemTexto(paciente, "55" + paciente.getNumeros().get(i));
            Thread.sleep(10000L);
        }


    }


    private void enviandoMensagemTexto(PacienteMR pacienteMR, String numero) {

        whatsappFuture.thenAccept(whatsapp -> {
            var pacientePersistido = salvandoIncialmente(pacienteMR);

            whatsapp.addListener(new ListenerNovaMensagem(numero, pacienteRepository, pacientePersistido));


            try {

                if (!whatsapp.isConnected()) {
                    System.err.println("O WhatsApp não está conectado.");
                    return;
                }
                System.out.println("Enviando mensagem para: " + numero);
                var contactJid = Jid.of(numero);

                String mensagem1 = String.format(
                        "Olá %S , somos da Secretária de saúde de Vitoria de Santo Antão.%n%n" +
                                "Estamos felizes em informá-lo sobre sua consulta ou exame: %n%S.%n%n" +
                                "Gostaríamos de saber se você ainda tem interesse.%n%n" +
                                "Por favor, responda SIM se estiver interessado, ou NÃO se não estiver. Caso não possua interesse, pedimos gentilmente que forneça sua justificativa..%n%n" +
                                "Aguardamos sua resposta.%nAtenciosamente, Regulação de saúde."
                        , pacienteMR.getNome() , pacienteMR.getConsulta());

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

    public String getQrCode() {
        return qrCode;
    }




    private Paciente salvandoIncialmente(PacienteMR pacienteMR) {
        if (!pacienteSetStringUUID.contains(pacienteMR.getId().toString())) {
            var paciente = disassembleToObject(pacienteMR);
            var pacientePersistido = pacienteRepository.save(paciente);
            pacienteSetStringUUID.add(pacienteMR.getId().toString());
            return pacientePersistido;
        }
        return pacienteRepository.findBycodigo(pacienteMR.getId().toString());
    }


    /*
        Método para converte o modelo representacional (Input de dados)
        no entity que vai ser persistido no banco de dados
     */
    private static Paciente disassembleToObject(PacienteMR pacienteMR) {
        var paciente = new Paciente();
        paciente.setCodigo(pacienteMR.getId().toString());
        paciente.setNome(pacienteMR.getNome());
        paciente.setNumero(pacienteMR.getNumeros().getFirst());
        paciente.setConsulta(pacienteMR.getConsulta());
        paciente.setDataConsulta(pacienteMR.getData());
        paciente.setMotivo("AGUARDANDO");
        paciente.setBairro(pacienteMR.getBairro());
        return paciente;
    }

}
