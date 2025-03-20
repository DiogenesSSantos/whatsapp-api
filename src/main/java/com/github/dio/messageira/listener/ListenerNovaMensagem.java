package com.github.dio.messageira.listener;

import com.github.dio.messageira.infraestruct.filaService.FIlaService;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.standard.TextMessage;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashSet;
import java.util.Set;


@RegisterListener
@EnableAsync
public class ListenerNovaMensagem implements Listener {

    private EncapsuladoPaciente encapsuladoPaciente;

    public static Set<String> uuidUnicoUsuarioSet = new HashSet<String>();
    private FIlaService filaService;


    public ListenerNovaMensagem(String numeroUsuario, PacienteRepository pacienteRepository, Paciente paciente) {
        encapsuladoPaciente = new EncapsuladoPaciente(numeroUsuario , paciente.getCodigo(), paciente);
        filaService = new FIlaService(pacienteRepository);
    }


    @SneakyThrows
    @Override
    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {
        String mensagemUsuario = null;
        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();


        if (!jidNumeroUsuario.equals(encapsuladoPaciente.getNumeroUsuario())) {
            return;
        }

        if (info.message().content() instanceof TextMessage textMessage) {
            mensagemUsuario = textMessage.text();
        }

        if (!(info.message().content() instanceof TextMessage textMessage)) {
            if (mensagemUsuario == null && jidNumeroUsuario.equals(encapsuladoPaciente.getNumeroUsuario())) {
                whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), String.format("NÃO ACEITAMOS MENSAGENS DE ÁUDIO, FOTOS, VÍDEOS OU FIGURINHAS COMO OPÇÃO.%n%n" +
                        "Por favor, responda com:%n%n" +
                        "SIM (caso tenha interesse na consulta/exame).%n%n" +
                        "NÃO (caso não tenha interesse na consulta/exame)."));
            }

            encapsuladoPaciente.setMotivoDesistencia(false);
            return;
        }

        if (encapsuladoPaciente.getMotivoDesistencia() && info.message().content() instanceof TextMessage) {

            String motivo = mensagemUsuario;
            if (motivo.matches("[a-zA-Z0-9 À-ÿ.,!?]+")) {
                whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), String.format("MOTIVO : %S.%n%nMuito obrigado, o encaminhamento será arquivado e removido da fila.", motivo));
                encapsuladoPaciente.setMotivoDesistencia(false);
                whatsapp.removeListener(this);

                if (!uuidUnicoUsuarioSet.contains(encapsuladoPaciente.getUuidUnicoUsuario())) {
                    uuidUnicoUsuarioSet.add(encapsuladoPaciente.getUuidUnicoUsuario());
                    filaService.excutarPersistencia(motivo, encapsuladoPaciente.getPaciente());
                }
                return;
            }
            encapsuladoPaciente.setMotivoDesistencia(false);
        }


        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao") &&
                !mensagemUsuario.equalsIgnoreCase("Não")) {
            whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), String.format("Por favor, responda com:%n%n" +
                    "SIM (caso tenha interesse na consulta/exame).%n%n" +
                    "NÃO (caso não tenha interesse na consulta/exame)."));
        }


        if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
            whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), String.format("Olá %S, " +
                    "Estamos felizes em saber que você tem interesse na consulta/exame: %S.%nEstá marcado.%n%n" +
                    "Compareça AMANHÃ entre 08:00 as 14:00 com cartão SUS no setor de regulação da Secretaria de Saúde de vitória de santo antão.%n%n" +
                    "A data da consulta/exame só estará disponível no momento da entrega do seu comprovante de agendamento.%n%n" +
                    "Aguardamos sua presença.%n" +
                    "Atenciosamente, Regulação de Saúde.", encapsuladoPaciente.getPaciente().getNome(),
                    encapsuladoPaciente.getPaciente().getConsulta()));
            whatsapp.removeListener(this);


            if (!uuidUnicoUsuarioSet.contains(encapsuladoPaciente.getUuidUnicoUsuario())) {
                uuidUnicoUsuarioSet.add(encapsuladoPaciente.getUuidUnicoUsuario());
                filaService.excutarPersistencia("ACEITO", encapsuladoPaciente.getPaciente());
            }

        }


        if (mensagemUsuario.equalsIgnoreCase("nao")
                || mensagemUsuario.equalsIgnoreCase("não")
                || mensagemUsuario.equalsIgnoreCase("naõ")
                || mensagemUsuario.equalsIgnoreCase("ñ")) {
            whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), "Coloque o motivo da desistência abaixo : ");
            encapsuladoPaciente.setMotivoDesistencia(true);
        }

    }

}






class EncapsuladoPaciente {
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;
    private String uuidUnicoUsuario;
    private Paciente paciente;


    public EncapsuladoPaciente(String numeroUsuario,String uuidUnicoUsuario, Paciente paciente) {
        this.numeroUsuario = "+"+numeroUsuario;
        this.uuidUnicoUsuario = uuidUnicoUsuario;
        this.paciente = paciente;
    }

    public String getNumeroUsuario() {
        return numeroUsuario;
    }

    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    public Boolean getMotivoDesistencia() {
        return motivoDesistencia;
    }

    public void setMotivoDesistencia(Boolean motivoDesistencia) {
        this.motivoDesistencia = motivoDesistencia;
    }

    public String getUuidUnicoUsuario() {
        return uuidUnicoUsuario;
    }

    public void setUuidUnicoUsuario(String uuidUnicoUsuario) {
        this.uuidUnicoUsuario = uuidUnicoUsuario;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}



