package com.github.dio.messageira.listener;

import com.github.dio.messageira.infraestruct.filaService.FIlaService;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.model.PacienteEncapsuladoNaoRespondido;
import com.github.dio.messageira.repository.PacienteRepository;
import com.github.dio.messageira.service.WhatsappService;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
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
import java.util.concurrent.LinkedBlockingQueue;


@EnableAsync
public class ListenerNovaMensagem implements Listener{
    private PacienteRP pacienteRPStatic;
    public static final Set<String> uuidUnicoUsuarioSet = new ConcurrentSkipListSet();
    private LinkedBlockingQueue<ListenerNovaMensagem> linkeBlockingQueueWhatsAppService;
    private PacienteEncapsuladoNaoRespondido pacienteEncapsuladoNaoRespondido;


    private FIlaService filaService;

    public ListenerNovaMensagem(){

    }

    public ListenerNovaMensagem(String numeroUsuario, PacienteRepository pacienteRepository,
                                Paciente paciente, LinkedBlockingQueue<ListenerNovaMensagem> linkeBlockingQueueWhatsAppService ,  PacienteEncapsuladoNaoRespondido PacienteNaoRespondio , FIlaService filaService ) {

        this.pacienteRPStatic = new PacienteRP(numeroUsuario, paciente.getCodigo(), paciente);
        this.filaService = filaService ;
        this.pacienteEncapsuladoNaoRespondido = PacienteNaoRespondio;
        this.linkeBlockingQueueWhatsAppService = linkeBlockingQueueWhatsAppService;


    }

    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {


        String mensagemUsuario = null;
//        String jidNumeroUsuario = info.senderJid().toSimpleJid().toPhoneNumber().get();
        String jidNumeroUsuario = "+"+info.senderJid().toJid().user();
        if (jidNumeroUsuario.equals(this.pacienteRPStatic.getNumeroUsuario())) {
            Message mensagem = info.message().content();
            if (mensagem instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) mensagem;
                mensagemUsuario = textMessage.text();
            }

            mensagem = info.message().content();

            if (!(mensagem instanceof TextMessage)) {
                if (mensagemUsuario == null && jidNumeroUsuario.equals(this.pacienteRPStatic.getNumeroUsuario())) {
                    whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("NÃO ACEITAMOS MENSAGENS DE ÁUDIO, FOTOS, VÍDEOS OU FIGURINHAS COMO OPÇÃO.%n%nPor favor, responda com:%n%nSIM (caso tenha interesse na consulta/exame).%n%nNÃO (caso não tenha interesse na consulta/exame)."));
                }
                this.pacienteRPStatic.setMotivoDesistencia(false);
            } else {
                TextMessage textMessage = (TextMessage) mensagem;
                if (this.pacienteRPStatic.getMotivoDesistencia() && info.message().content() instanceof TextMessage) {
                    if (mensagemUsuario.matches("[a-zA-Z0-9 À-ÿ.,!?]+")) {
                        whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("MOTIVO DA SUA DESISTÊNCIA : %S.%n%nMuito obrigado, o encaminhamento será arquivado e removido da fila.", mensagemUsuario));
                        this.pacienteRPStatic.setMotivoDesistencia(false);
                        whatsapp.removeListener(this);
                        removendoDaFila(mensagemUsuario, pacienteRPStatic);
                        this.linkeBlockingQueueWhatsAppService.remove(this);
                        WhatsappService.pacienteList.remove(pacienteEncapsuladoNaoRespondido);
                        resetThis();
                        return;
                    }

                    this.pacienteRPStatic.setMotivoDesistencia(false);
                }


                isRespostaValida(whatsapp, mensagemUsuario);
                isSim(whatsapp, mensagemUsuario);
                isNao(whatsapp, mensagemUsuario);
            }
        }
    }





    private void isRespostaValida(Whatsapp whatsapp, String mensagemUsuario) {
        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao") && !mensagemUsuario.equalsIgnoreCase("Não")) {
            whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("Por favor, responda com:%n%nSIM (caso tenha interesse na consulta/exame).%n%nNÃO (caso não tenha interesse na consulta/exame)."));
        }
    }


    private void isSim(Whatsapp whatsapp, String mensagemUsuario) {
        if (mensagemUsuario.equalsIgnoreCase("sim") || mensagemUsuario.equalsIgnoreCase("s")) {
            if (isFinalSemana()) {
                whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("Olá %S, Estamos felizes em saber que você tem interesse na consulta/exame: %S.%nEstá marcado.%n%nCompareça SEGUNDA-FEIRA entre o horário 08:00 as 14:00 com cartão SUS no setor de regulação da Secretaria de Saúde de vitória de santo antão.%n%nA data da consulta/exame só estará disponível no momento da entrega do seu comprovante de agendamento.%n%nAguardamos sua presença.%nAtenciosamente, Regulação de Saúde.", this.pacienteRPStatic.getPaciente().getNome(), this.pacienteRPStatic.getPaciente().getConsulta()));
                whatsapp.removeListener(this);
                removendoDaFila("ACEITO", this.pacienteRPStatic);
                this.linkeBlockingQueueWhatsAppService.remove(this);
                WhatsappService.pacienteList.remove(pacienteEncapsuladoNaoRespondido);
                resetThis();
            } else {
                whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("Olá %S, Estamos felizes em saber que você tem interesse na consulta/exame: %S.%nEstá marcado.%n%nCompareça AMANHÃ entre o horário 08:00 as 14:00 com cartão SUS no setor de regulação da Secretaria de Saúde de Vitória de Santo Antão.%n%nA data da consulta/exame só estará disponível no momento da entrega do seu comprovante de agendamento.%n%nAguardamos sua presença.%nAtenciosamente, Regulação de Saúde.", this.pacienteRPStatic.getPaciente().getNome(), this.pacienteRPStatic.getPaciente().getConsulta()));
                whatsapp.removeListener(this);
                removendoDaFila("ACEITO", pacienteRPStatic);
                this.linkeBlockingQueueWhatsAppService.remove(this);
                WhatsappService.pacienteList.remove(pacienteEncapsuladoNaoRespondido);
                resetThis();
            }
        }
    }

    private void isNao(Whatsapp whatsapp, String mensagemUsuario) {
        if (mensagemUsuario.equalsIgnoreCase("nao") || mensagemUsuario.equalsIgnoreCase("não") || mensagemUsuario.equalsIgnoreCase("naõ") || mensagemUsuario.equalsIgnoreCase("ñ")) {
            whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), "Coloque o motivo da desistência abaixo...");
            this.pacienteRPStatic.setMotivoDesistencia(true);
        }
    }


    private void removendoDaFila(String resposta, PacienteRP pacienteRPStatic) {
        if (!uuidUnicoUsuarioSet.contains(this.pacienteRPStatic.getUuidUnicoUsuario())) {
            uuidUnicoUsuarioSet.add(this.pacienteRPStatic.getUuidUnicoUsuario());
            this.filaService.excutarPersistencia(resposta, this.pacienteRPStatic.getPaciente(), pacienteRPStatic.getNumeroUsuario());
        }
    }


    private static boolean isFinalSemana() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Recife")).plusDays(1L);
        return data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SATURDAY.toString()) || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SUNDAY.toString());
    }


    public void resetThis() {
        this.pacienteRPStatic = null;
        this.filaService = null;
        this.pacienteEncapsuladoNaoRespondido = null;
        this.linkeBlockingQueueWhatsAppService = null;

    }

}


