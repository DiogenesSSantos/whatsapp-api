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
import it.auties.whatsapp.util.ConcurrentLinkedSet;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@RegisterListener
@EnableAsync
public class ListenerNovaMensagemStatic implements Listener {
    private static final Map<String, EncapsuladoPacienteStatic> pacientesMonitorados = new ConcurrentHashMap<>();
    public static Set<String> uuidUnicoUsuarioSet = new ConcurrentLinkedSet();
    private static FIlaService filaService;
    private static PacienteRepository pacienteRepository;


    // Método para injetar PacienteRepository
    public static void setPacienteRepository(PacienteRepository repository) {
        pacienteRepository = repository;
        filaService = new FIlaService(repository);
    }



    public static void adicionarPaciente(String numeroUsuario, Paciente paciente) {
        pacientesMonitorados.put(numeroUsuario, new EncapsuladoPacienteStatic(numeroUsuario, paciente.getCodigo(), paciente));
    }


    @Override
    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {
        String numeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber();
        EncapsuladoPacienteStatic encapsuladoPaciente = pacientesMonitorados.get(numeroUsuario);

        if (encapsuladoPaciente != null) {
            processarMensagem(whatsapp, encapsuladoPaciente, info);
        }
    }

    private static void processarMensagem(Whatsapp whatsapp, EncapsuladoPacienteStatic encapsuladoPaciente, MessageInfo<?> info) {
        String mensagemUsuario = (info.message().content() instanceof TextMessage)
                ? ((TextMessage) info.message().content()).text()
                : null;

        if (mensagemUsuario == null) {
            whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), "Responda apenas com SIM ou NÃO.");
            return;
        }

        if (mensagemUsuario.equalsIgnoreCase("sim")) {
            responderConfirmacao(whatsapp, encapsuladoPaciente);
        } else if (mensagemUsuario.equalsIgnoreCase("não")) {
            whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), "Digite o motivo da desistência:");
            encapsuladoPaciente.setMotivoDesistencia(true);
        } else if (encapsuladoPaciente.getMotivoDesistencia() && mensagemUsuario.matches("[a-zA-Z0-9 À-ÿ.,!?]+")) {
            whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), "Motivo registrado. Obrigado.");
            encapsuladoPaciente.setMotivoDesistencia(false);
            uuidUnicoUsuarioSet.add(encapsuladoPaciente.getUuidUnicoUsuario());
            filaService.excutarPersistencia(mensagemUsuario, encapsuladoPaciente.getPaciente(), encapsuladoPaciente.getNumeroUsuario());
        }
    }

    private static void responderConfirmacao(Whatsapp whatsapp, EncapsuladoPacienteStatic encapsuladoPaciente) {
        String mensagem;

        if (isFinalSemana()) {
            mensagem = String.format("Olá %s, sua consulta/exame %s está confirmada. Compareça SEGUNDA-FEIRA entre 08:00 e 14:00.",
                    encapsuladoPaciente.getPaciente().getNome(), encapsuladoPaciente.getPaciente().getConsulta());
        } else {
            mensagem = String.format("Olá %s, sua consulta/exame %s está confirmada. Compareça AMANHÃ entre 08:00 e 14:00.",
                    encapsuladoPaciente.getPaciente().getNome(), encapsuladoPaciente.getPaciente().getConsulta());
        }

        whatsapp.sendMessage(Jid.of(encapsuladoPaciente.getNumeroUsuario()), mensagem);
        uuidUnicoUsuarioSet.add(encapsuladoPaciente.getUuidUnicoUsuario());
        filaService.excutarPersistencia("ACEITO", encapsuladoPaciente.getPaciente(), encapsuladoPaciente.getNumeroUsuario());
    }





    private static boolean isFinalSemana() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Recife")).plusDays(1L);
        return data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SATURDAY.toString()) || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SUNDAY.toString());
    }
}


class EncapsuladoPacienteStatic {
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;
    private String uuidUnicoUsuario;
    private Paciente paciente;

    public EncapsuladoPacienteStatic(String numeroUsuario, String uuidUnicoUsuario, Paciente paciente) {
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


