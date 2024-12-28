package com.github.dio.messageira.service;

import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.listener.ListenerNovaMensagem;
import it.auties.whatsapp.api.PairingCodeHandler;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappCustomBuilder;
import it.auties.whatsapp.model.button.base.Button;
import it.auties.whatsapp.model.button.base.ButtonBody;
import it.auties.whatsapp.model.button.base.ButtonText;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.MessageIndexInfo;
import it.auties.whatsapp.model.info.NativeFlowInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.button.ButtonsMessageBuilder;
import it.auties.whatsapp.model.message.button.ButtonsMessageHeader;
import it.auties.whatsapp.model.message.button.ButtonsMessageHeaderText;
import it.auties.whatsapp.model.message.model.Message;
import it.auties.whatsapp.model.message.model.MessageContainer;
import it.auties.whatsapp.model.message.model.MessageContainerBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTMLDocument;
import java.util.*;
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
//                .addListener(new ListenerNovaMensagem())
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
            enviandoMensagemTexto("5581"+paciente.getNumeros().get(i), paciente.getNome(), paciente.getTipoConsulta());
            Thread.sleep(10000L);
        }
    } public void enviarMensagemBotao(PacienteMR paciente) throws InterruptedException {
        for (int i = 0; i < paciente.getNumeros().size(); i++) {
            enviandoMensagemComBotao(paciente.getNumeros().get(i));
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




    private static void enviandoMensagemTexto(String numero, String nomeUsuario, String tipoConsulta) {

        whatsappFuture.thenAccept(whatsapp -> {

            whatsapp.addListener(new ListenerNovaMensagem(whatsapp, nomeUsuario , numero));


            try {
                if (!whatsapp.isConnected()) {
                    System.err.println("O WhatsApp não está conectado.");
                    return;
                }
                System.out.println("Enviando mensagem para: " + numero);
                var contactJid = Jid.of(numero);

                String mensagemTest = "Test";
                String mensagem1 = String.format(
                        "Boa tarde! Somos da SECRETARIA DE SAÚDE DE VITÓRIA DE SANTO ANTÃO. Venho, por meio desta mensagem, " +
                                "informar sobre um comprovante de agendamento para:%n%n" +
                                "Consulta: %S.%n" +
                                "Paciente: %s%nMotivo: CIRURGIA DE PTERÍGIO OU CATARATA.%n%n" +
                                "Por favor, pegar este comprovante de agendamento NA TERÇA-FEIRA, dia 17/12/2024, " +
                                "no horário entre 12:00 e 17:00, na SECRETARIA DE SAÚDE.%n%n" +
                                "ME CONFIRME COM OK, caso possua interesse.%n%n" +
                                "OBS: Caso não conheça o paciente ou o mesmo não tenha mais interesse na consulta, desconsidere esta mensagem.%n%n" +
                                "REFORÇANDO, ME CONFIRME COM !!!OK!!!. Se não me confirmar, não será marcada a consulta.%n%n" +
                                "Para pegar no dia 17/12/24 (TERÇA-FEIRA), no horário informado, a partir das 12:00 até 17:00. " +
                                "Caso chegue antes da data ou horário informado, terá que aguardar.%n%n" +
                                "Agradeço a compreensão.",
                        tipoConsulta,nomeUsuario
                );
                whatsapp.sendMessage(contactJid, mensagemTest).thenRun(() -> {
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
            NativeFlowInfo nativeFlowInfo = new NativeFlowInfo("Test" , "TESTE O NATIVE FLOR");
            ButtonText buttonTextSim = new ButtonText("SIM");
            ButtonText buttonTextNao = new ButtonText("NÃO");

            Button buttonSim = new Button("Sim" , Optional.of(buttonTextSim) , Optional.of(nativeFlowInfo) , ButtonBody.Type.NATIVE_FLOW);
            Button buttonNao = new Button("Não" , Optional.of(buttonTextNao) , Optional.of(nativeFlowInfo) , ButtonBody.Type.NATIVE_FLOW);

            ButtonsMessageBuilder buttonsMessageBuilder = new ButtonsMessageBuilder();
            buttonsMessageBuilder.body("SELECIONE UMA OPÇÃO ABAIXO");
            buttonsMessageBuilder.headerType(ButtonsMessageHeader.Type.TEXT);
            buttonsMessageBuilder.headerText(ButtonsMessageHeaderText.of("AQUI VAI A MENSAGEM EXPLICANDO O TIPO CONSULTA E NOME PACIENTE"));
            buttonsMessageBuilder.footer("AGRADEÇO A COMPREENSAÕ");
            buttonsMessageBuilder.buttons(List.of(buttonSim , buttonNao));

            MessageContainer container = new MessageContainerBuilder()
                    .buttonsMessage(buttonsMessageBuilder.build())
                    .build();

            whatsapp.sendMessage(numeroJid , container);

        }).exceptionally(ex -> {
            System.err.println("Falha ao obter a instância do WhatsApp: " + ex.getMessage());
            return null;
        });
    }



}
