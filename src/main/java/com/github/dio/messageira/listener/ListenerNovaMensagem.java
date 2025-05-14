package com.github.dio.messageira.listener;

import com.github.dio.messageira.infraestruct.filaService.FIlaService;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.Message;
import it.auties.whatsapp.model.message.standard.TextMessage;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;


@RegisterListener
@EnableAsync
public class ListenerNovaMensagem implements Listener {
    private EncapsuladoPacienteStatic encapsuladoPacienteStatic;
    public static Set<String> uuidUnicoUsuarioSet = new ConcurrentSkipListSet();
    private FIlaService filaService;

    public ListenerNovaMensagem(String numeroUsuario, PacienteRepository pacienteRepository, Paciente paciente) {
        this.encapsuladoPacienteStatic = new EncapsuladoPacienteStatic(numeroUsuario, paciente.getCodigo(), paciente);
        this.filaService = new FIlaService(pacienteRepository);
    }

    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {
        String mensagemUsuario = null;
        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();
        if (jidNumeroUsuario.equals(this.encapsuladoPacienteStatic.getNumeroUsuario())) {
            Message var6 = info.message().content();
            if (var6 instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) var6;
                mensagemUsuario = textMessage.text();
            }

            var6 = info.message().content();
            if (!(var6 instanceof TextMessage)) {
                if (mensagemUsuario == null && jidNumeroUsuario.equals(this.encapsuladoPacienteStatic.getNumeroUsuario())) {
                    whatsapp.sendMessage(Jid.of(this.encapsuladoPacienteStatic.getNumeroUsuario()), String.format("NÃO ACEITAMOS MENSAGENS DE ÁUDIO, FOTOS, VÍDEOS OU FIGURINHAS COMO OPÇÃO.%n%nPor favor, responda com:%n%nSIM (caso tenha interesse na consulta/exame).%n%nNÃO (caso não tenha interesse na consulta/exame)."));
                }

                this.encapsuladoPacienteStatic.setMotivoDesistencia(false);
            } else {
                TextMessage textMessage = (TextMessage) var6;
                if (this.encapsuladoPacienteStatic.getMotivoDesistencia() && info.message().content() instanceof TextMessage) {
                    if (mensagemUsuario.matches("[a-zA-Z0-9 À-ÿ.,!?]+")) {
                        whatsapp.sendMessage(Jid.of(this.encapsuladoPacienteStatic.getNumeroUsuario()), String.format("MOTIVO DA SUA DESISTÊNCIA : %S.%n%nMuito obrigado, o encaminhamento será arquivado e removido da fila.", mensagemUsuario));
                        this.encapsuladoPacienteStatic.setMotivoDesistencia(false);
                        whatsapp.removeListener(this);
                        if (!uuidUnicoUsuarioSet.contains(this.encapsuladoPacienteStatic.getUuidUnicoUsuario())) {
                            uuidUnicoUsuarioSet.add(this.encapsuladoPacienteStatic.getUuidUnicoUsuario());
                            this.filaService.excutarPersistencia(mensagemUsuario, this.encapsuladoPacienteStatic.getPaciente() , encapsuladoPacienteStatic.getNumeroUsuario());
                        }

                        return;
                    }

                    this.encapsuladoPacienteStatic.setMotivoDesistencia(false);
                }

                if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao") && !mensagemUsuario.equalsIgnoreCase("Não")) {
                    whatsapp.sendMessage(Jid.of(this.encapsuladoPacienteStatic.getNumeroUsuario()), String.format("Por favor, responda com:%n%nSIM (caso tenha interesse na consulta/exame).%n%nNÃO (caso não tenha interesse na consulta/exame)."));
                }

                if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
                    if (isFinalSemana()) {
                        whatsapp.sendMessage(Jid.of(this.encapsuladoPacienteStatic.getNumeroUsuario()), String.format("Olá %S, Estamos felizes em saber que você tem interesse na consulta/exame: %S.%nEstá marcado.%n%nCompareça SEGUNDA-FEIRA entre o horário 08:00 as 14:00 com cartão SUS no setor de regulação da Secretaria de Saúde de vitória de santo antão.%n%nA data da consulta/exame só estará disponível no momento da entrega do seu comprovante de agendamento.%n%nAguardamos sua presença.%nAtenciosamente, Regulação de Saúde.", this.encapsuladoPacienteStatic.getPaciente().getNome(), this.encapsuladoPacienteStatic.getPaciente().getConsulta()));
                        whatsapp.removeListener(this);
                        if (!uuidUnicoUsuarioSet.contains(this.encapsuladoPacienteStatic.getUuidUnicoUsuario())) {
                            uuidUnicoUsuarioSet.add(this.encapsuladoPacienteStatic.getUuidUnicoUsuario());
                            this.filaService.excutarPersistencia("ACEITO", this.encapsuladoPacienteStatic.getPaciente() , this.encapsuladoPacienteStatic.getNumeroUsuario());
                        }
                    } else {
                        whatsapp.sendMessage(Jid.of(this.encapsuladoPacienteStatic.getNumeroUsuario()), String.format("Olá %S, Estamos felizes em saber que você tem interesse na consulta/exame: %S.%nEstá marcado.%n%nCompareça AMANHÃ entre o horário 08:00 as 14:00 com cartão SUS no setor de regulação da Secretaria de Saúde de Vitória de Santo Antão.%n%nA data da consulta/exame só estará disponível no momento da entrega do seu comprovante de agendamento.%n%nAguardamos sua presença.%nAtenciosamente, Regulação de Saúde.", this.encapsuladoPacienteStatic.getPaciente().getNome(), this.encapsuladoPacienteStatic.getPaciente().getConsulta()));
                        whatsapp.removeListener(this);
                        if (!uuidUnicoUsuarioSet.contains(this.encapsuladoPacienteStatic.getUuidUnicoUsuario())) {
                            uuidUnicoUsuarioSet.add(this.encapsuladoPacienteStatic.getUuidUnicoUsuario());
                            this.filaService.excutarPersistencia("ACEITO", this.encapsuladoPacienteStatic.getPaciente() , encapsuladoPacienteStatic.getNumeroUsuario());
                        }
                    }
                }

                if (mensagemUsuario.equalsIgnoreCase("nao") || mensagemUsuario.equalsIgnoreCase("não") || mensagemUsuario.equalsIgnoreCase("naõ") || mensagemUsuario.equalsIgnoreCase("ñ")) {
                    whatsapp.sendMessage(Jid.of(this.encapsuladoPacienteStatic.getNumeroUsuario()), "Coloque o motivo da desistência abaixo...");
                    this.encapsuladoPacienteStatic.setMotivoDesistencia(true);
                }

            }
        }
    }

    private static boolean isFinalSemana() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Recife")).plusDays(1L);
        return data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SATURDAY.toString()) || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SUNDAY.toString());
    }
}


class EncapsuladoPaciente {
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;
    private String uuidUnicoUsuario;
    private Paciente paciente;

    public EncapsuladoPaciente(String numeroUsuario, String uuidUnicoUsuario, Paciente paciente) {
        this.numeroUsuario = "+" + numeroUsuario;
        this.uuidUnicoUsuario = uuidUnicoUsuario;
        this.paciente = paciente;
    }

    public String getNumeroUsuario() {
        return this.numeroUsuario;
    }

    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    public Boolean getMotivoDesistencia() {
        return this.motivoDesistencia;
    }

    public void setMotivoDesistencia(Boolean motivoDesistencia) {
        this.motivoDesistencia = motivoDesistencia;
    }

    public String getUuidUnicoUsuario() {
        return this.uuidUnicoUsuario;
    }

    public void setUuidUnicoUsuario(String uuidUnicoUsuario) {
        this.uuidUnicoUsuario = uuidUnicoUsuario;
    }

    public Paciente getPaciente() {
        return this.paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }



}


