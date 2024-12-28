package com.github.dio.messageira.listener;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.standard.TextMessage;
import lombok.SneakyThrows;

@RegisterListener
public class ListenerNovaMensagem implements Listener {

    private  Whatsapp whatsapp;
    private String nomeUsuario;
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;


    public ListenerNovaMensagem (Whatsapp whatsapp , String nomeUsuario , String numeroUsuario) {
        this.whatsapp = whatsapp;
        this.nomeUsuario = nomeUsuario;
        this.numeroUsuario = "+"+numeroUsuario;
    }

    @SneakyThrows
    @Override
    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {
        String mensagemUsuario = null;
        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();

        if (!jidNumeroUsuario.equals(numeroUsuario)) {
            return;
        }

        if(info.message().content() instanceof TextMessage textMessage) {
            mensagemUsuario = textMessage.text();
        }

        if (!(info.message().content() instanceof  TextMessage textMessage)) {
            if (mensagemUsuario == null && jidNumeroUsuario.equals(numeroUsuario) ){
                whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("NÃO ACEITAMOS MENSAGEM DE AUDIO, FOTOS, VIDEOS OU FIGURINHAS COMO OPÇÃO.%n%n" +
                        "(sim) caso tenha interesse na consulta.%n%n"+
                        "(não) caso desistencia e depois digite o motivo da sua desistencia abaixo :"));
            }

            motivoDesistencia = false;
            return;
        }

        if (motivoDesistencia && info.message().content() instanceof  TextMessage) {
            //Aqui registra o motivo da desistencia no campo motivo do usuario;
            String motivo = mensagemUsuario;
            if (motivo.matches("[a-zA-Z0-9 À-ÿ.,!?]+")){
                whatsapp.sendMessage(Jid.of(numeroUsuario) ,String.format("MOTIVO : %S.%n%nMuito obrigado, o encaminhamento será arquivado e removido da fila." , motivo));
                motivoDesistencia = false;
                whatsapp.removeListener(this);
                return;
            }
            motivoDesistencia = false;
        }


        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao")){
            whatsapp.sendMessage(Jid.of(numeroUsuario), String.format("Por favor digite uma das opções:%n%n" +
                    "(sim) caso tenha interesse na consulta.%n%n"+
                    "(não) caso desistencia e depois digite o motivo da sua desistencia abaixo :"));
        }

        if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario) , "Está marcado, pode vim pegar no dia e horário que foi estipulado anteriomente.");
            whatsapp.removeListener(this);
        }

        if (mensagemUsuario.equalsIgnoreCase("nao")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario) , "Coloque o motivo da desistencia abaixo : ");
            motivoDesistencia = true;
        }


    }
}
