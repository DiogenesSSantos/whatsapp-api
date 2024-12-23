package com.github.dio.messageira.listener;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.Info;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.MessageCategory;
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
        String mensagemUsuario = info.message().textWithNoContextMessage().get();
        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();

        if (!jidNumeroUsuario.equals(numeroUsuario)) {
            return;
       }

        if (motivoDesistencia) {
            //Aqui registra o motivo da desistencia no campo motivo do usuario;
            String motivo = mensagemUsuario;
            whatsapp.sendMessage(Jid.of(numeroUsuario) ,String.format("MOTIVO : %S%n%n Muito obrigado o encaminhamento será arquivado e removido da fila." , motivo));
            motivoDesistencia = false;
            whatsapp.removeListener(this);
            return;
        }

        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao")){
            whatsapp.sendMessage(Jid.of(numeroUsuario), "A RESPOSTA DEVE SER SIM OU NÃO");
        }

        if (mensagemUsuario.equalsIgnoreCase("sim")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario) , "ESTÁ MARCADO VENHA PEGAR ATÉ SEXTA FEIRA NO HORARIO ENTRE 08:00 até as 14:00");
            whatsapp.removeListener(this);
        }

        if (mensagemUsuario.equalsIgnoreCase("nao")) {
            whatsapp.sendMessage(Jid.of(numeroUsuario) , " COLOQUE O MOTIVO DA DESISTENCIA ABAIXO");
            motivoDesistencia = true;
        }


    }
}
