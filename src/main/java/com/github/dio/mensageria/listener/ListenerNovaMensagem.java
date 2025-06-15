package com.github.dio.mensageria.listener;

import com.github.dio.mensageria.infraestrutura.filaService.FilaService;
import com.github.dio.mensageria.model.Paciente;
import com.github.dio.mensageria.model.PacienteEncapsuladoNaoRespondido;
import com.github.dio.mensageria.repository.PacienteRepository;
import com.github.dio.mensageria.service.WhatsappService;
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


/**
 * Classe que implementa o {@link Listener}
 *
 * @author diogenesssantos.
 * A classe responsável para observar todas a mensagem respondida pelo paciente,
 * utilizamos a biblioteca <a href="https://github.com/Auties00/Cobalt">auties/Cobalt</a>
 * @Override no método onNewMessage().
 */
@EnableAsync
public class ListenerNovaMensagem implements Listener {
    /**
     * Na regra de negócio, temos um set por uuid do {@link Paciente}, nos auxiliar em algumas validações.
     */
    public static final Set<String> uuidUnicoUsuarioSet = new ConcurrentSkipListSet();

    private PacienteRP pacienteRPStatic;
    private LinkedBlockingQueue<ListenerNovaMensagem> linkeBlockingQueueWhatsAppService;
    private PacienteEncapsuladoNaoRespondido pacienteEncapsuladoNaoRespondido;
    private FilaService filaService;


    /**
     * Inicialização padrão sem argumentos para serialização.
     */
    public ListenerNovaMensagem() {
    }

    /**
     * Para uma inicialização de {@link ListenerNovaMensagem} precisamos desses parâmetros para aplicação
     * da regra de negócio.
     *
     * @param numeroUsuario                     o número usuario
     * @param pacienteRepository                a instancia paciente repository
     * @param paciente                          o paciente persistido anteriormente que possua um WhatsApp.
     * @param linkeBlockingQueueWhatsAppService o linkeblockingqueue do whatsappservice e importante devido à resposta                                          do usuário removê-lo da fila.
     * @param PacienteNaoRespondio              o pacientenaorespondio.
     * @param filaService                       O fila service aonde executaremos PRODUCE AND CONSUMER,                                         Leia para mais detalhe {@link FilaService}
     */
    public ListenerNovaMensagem(String numeroUsuario, PacienteRepository pacienteRepository,
                                Paciente paciente, LinkedBlockingQueue<ListenerNovaMensagem> linkeBlockingQueueWhatsAppService, PacienteEncapsuladoNaoRespondido PacienteNaoRespondio, FilaService filaService) {

        this.pacienteRPStatic = new PacienteRP(numeroUsuario, paciente.getCodigo(), paciente);
        this.filaService = filaService;
        this.pacienteEncapsuladoNaoRespondido = PacienteNaoRespondio;
        this.linkeBlockingQueueWhatsAppService = linkeBlockingQueueWhatsAppService;

    }

    /**
     * Esse é o método responsável para observar cada mensagem registrada pelo listener no {@link WhatsappService}
     *
     * @param whatsapp {@link Whatsapp}
     * @param info é mensagem encapsulada leia {@link Whatsapp}
     *
     *
     * Chamada interna desse método todos são private.
     * 1-validaResposta(Whatsapp whatsapp, String mensagemUsuario)->se a resposta não for cumprida na regra negócio é
     * enviada uma mensagem de aviso para usuário esperando uma resposta correta.
     *
     * 2-respostaSim(whatsapp, mensagemUsuario) -> dado uma resposta sim, usaremos o filaService junto com seu método
     * PRODUCE AND CONSUMER.
     *
     * 3-respostaNao(whatsapp, mensagemUsuario) -> dado uma resposta não, usaremos o filaService junto com seu método
     * PRODUCE AND CONSUMER.
     */
    @Override
    public void onNewMessage(Whatsapp whatsapp, MessageInfo<?> info) {


        String mensagemUsuario = null;
        String jidNumeroUsuario = "+" + info.senderJid().toJid().user();

        if (jidNumeroUsuario.equals(this.pacienteRPStatic.getNumeroUsuario())) {
            Message mensagem = info.message().content();

            if (mensagem instanceof TextMessage textMessage) {
                mensagemUsuario = textMessage.text();
            } else {
                whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("NÃO ACEITAMOS MENSAGENS DE ÁUDIO, FOTOS, VÍDEOS OU FIGURINHAS COMO OPÇÃO.%n%nPor favor, responda com:%n%nSIM (caso tenha interesse na consulta/exame).%n%nNÃO (caso não tenha interesse na consulta/exame)."));
                this.pacienteRPStatic.setMotivoDesistencia(false);
            }


            if (isRespostaMotivoDesistenciaValida(whatsapp, info, mensagemUsuario)) return;

            validaResposta(whatsapp, mensagemUsuario);
                respostaSim(whatsapp, mensagemUsuario);
                respostaNao(whatsapp, mensagemUsuario);
        }
    }




    private boolean isRespostaMotivoDesistenciaValida(Whatsapp whatsapp, MessageInfo<?> info, String mensagemUsuario) {
        if (this.pacienteRPStatic.getMotivoDesistencia() && info.message().content() instanceof TextMessage) {
            if (mensagemUsuario.matches("[a-zA-Z0-9 À-ÿ.,!?]+")) {
                whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("MOTIVO DA SUA DESISTÊNCIA : %S.%n%nMuito obrigado, o encaminhamento será arquivado e removido da fila.", mensagemUsuario));
                this.pacienteRPStatic.setMotivoDesistencia(false);
                whatsapp.removeListener(this);
                removendoDaFila(mensagemUsuario, pacienteRPStatic);
                this.linkeBlockingQueueWhatsAppService.remove(this);
                WhatsappService.pacienteList.remove(pacienteEncapsuladoNaoRespondido);
                resetThis();
                return true;
            }

            this.pacienteRPStatic.setMotivoDesistencia(false);
        }
        return false;
    }


    private void validaResposta(Whatsapp whatsapp, String mensagemUsuario) {
        if (!mensagemUsuario.equalsIgnoreCase("sim") && !mensagemUsuario.equalsIgnoreCase("nao") && !mensagemUsuario.equalsIgnoreCase("Não")) {
            whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), String.format("Por favor, responda com:%n%nSIM (caso tenha interesse na consulta/exame).%n%nNÃO (caso não tenha interesse na consulta/exame)."));
        }
    }


    private void respostaSim(Whatsapp whatsapp, String mensagemUsuario) {
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



    private void respostaNao(Whatsapp whatsapp, String mensagemUsuario) {
        if (mensagemUsuario.equalsIgnoreCase("nao") || mensagemUsuario.equalsIgnoreCase("não") || mensagemUsuario.equalsIgnoreCase("naõ") || mensagemUsuario.equalsIgnoreCase("ñ")) {
            whatsapp.sendMessage(Jid.of(this.pacienteRPStatic.getNumeroUsuario()), "Coloque o motivo da desistência abaixo...");
            this.pacienteRPStatic.setMotivoDesistencia(true);
        }
    }


    private static boolean isFinalSemana() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Recife")).plusDays(1L);
        return data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SATURDAY.toString()) || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SUNDAY.toString());
    }


    /**
     * @implNote
     * Funcionalidade desse método é para solucionar um problema das versões anteriores,
     * depois de um certo tempo tinha uma exceptio {@link ArrayIndexOutOfBoundsException}
     * que fazia nossa aplicação crashar, devido à  muitos objetos em memória mesmo eles não terem mais utilidade de uso
     * para {@link Listener},
     * então conhecimento adquirido na documentação java sobre GABARGE COLLECTION quando o objeto e referenciado null
     * em seus atributos ele fica elegível para limpeza de mémoria depois dessa implementação o error em novas versões
     * não veio mais acontecer.
     */
    public void resetThis() {
        this.pacienteRPStatic = null;
        this.filaService = null;
        this.pacienteEncapsuladoNaoRespondido = null;
        this.linkeBlockingQueueWhatsAppService = null;

    }



    private void removendoDaFila(String resposta, PacienteRP pacienteRPStatic) {
        if (!uuidUnicoUsuarioSet.contains(this.pacienteRPStatic.getUuidUnicoUsuario())) {
            uuidUnicoUsuarioSet.add(this.pacienteRPStatic.getUuidUnicoUsuario());
            this.filaService.executaProducaoEConsumo(resposta, this.pacienteRPStatic.getPaciente(), pacienteRPStatic.getNumeroUsuario());
        }
    }

}


