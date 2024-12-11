package com.github.dio.messageira.service;

import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import it.auties.whatsapp.api.PairingCodeHandler;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.button.base.Button;
import it.auties.whatsapp.model.button.base.ButtonBody;
import it.auties.whatsapp.model.button.base.ButtonText;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.MessageIndexInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.button.ButtonsMessageBuilder;
import it.auties.whatsapp.model.message.button.ButtonsMessageHeader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
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

//        System.out.println("DIGITE UM NUMERO DE TELEFONE COM CODIGO DO PAÍS E DDD, OBS NUMEROS DE " +
//                "PERNANBUCO NÃO COLOQUE O 9 A FRENTE ");
//        var numero = new Scanner(System.in).nextLong();
//        Whatsapp.webBuilder()
//                .lastConnection()
//                .unregistered(numero, PairingCodeHandler.toTerminal())
//                .addLoggedInListener(api -> {
//                    whatsappFuture.complete(api);
//                    System.out.printf("CONECTADO: %s%n", api.store().privacySettings());
//                })
//                .addDisconnectedListener(razao -> {
//                    whatsappFuture = new CompletableFuture<>();
//                    System.out.println("RAZÃO DISCONEXÃO : "+ razao.toString());
//                })
//                .addNewChatMessageListener(mensagem-> System.out.println("NOVA MENSAGEM : " + mensagem.toJson()))
//                .connect()
//                .thenRun(() -> System.out.println("CONECTADO NO SEU WHATSAPP"))
//                .exceptionally(throwable -> {
//                    System.err.println("ERRO AO CONECTAR NO ZAP : " + throwable.getMessage());
//                    throwable.getStackTrace();
//                    whatsappFuture.completeExceptionally(throwable);
//                    return null;
//                });

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


    public void enviarMensagem(PacienteMR paciente) throws InterruptedException {
        for (int i = 0; i < paciente.getNumeros().size(); i++) {
            enviandoMensagemTexto(paciente.getNumeros().get(i), paciente.getNome());
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


    public void enviarMensagemComBotao(String numero, String mensagem) {
        enviandoMensagemComBotao(numero);
    }


    private static void enviandoMensagemTexto(String numero, String nomeUsuario) {
        whatsappFuture.thenAccept(whatsapp -> {
            try {
                if (!whatsapp.isConnected()) {
                    System.err.println("O WhatsApp não está conectado.");
                    return;
                }
                System.out.println("Enviando mensagem para: " + numero);
                var contactJid = Jid.of(numero);
                String mensagem =
                        String.format( "Boa tarde somos da Secretaria de Saúde de Vitória de Santo Antão. Venho, por meio desta mensagem," +
                                " informar sobre um comprovante de agendamento para:%n%n" + "Consulta: Oftalmologista%n" + "Paciente: %s%nMotivo: CIRURGIA DE PTERÍGIO OU CATARATA.%n%n"
                                + "Por favor, pegar este comprovante de agendamento NA SEXTA-FEIRA, dia 13/12/24 horario entre 08:00 e 15:00, na Secretaria de Saúde setor de REGULAÇÃO.%n%n"
                                        + "ME CONFIRME COM OK, CASO POSSUA INTERESSE.%n%n" +
                                "OBS: E caso contrário não conheça o paciente ou o mesmo não tenha mais interesse na consulta, desconsidere esta mensagem.", nomeUsuario);


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

    //TODO REFATORARA ESSE METODO, AS MENSAGEM NÃO MOSTRANDO PARA MOBILE.
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


    public void capturadoMensagemUsuario() {
        whatsappFuture.thenAccept(whatsapp -> {

            // MessageIndexInfo

        });

    }


}
