
package com.github.dio.mensageria.service;


import com.github.dio.mensageria.model.modeloRepresentacional.PacienteMR;
import com.github.dio.mensageria.infraestrutura.assembler.AssemblerPaciente;
import com.github.dio.mensageria.infraestrutura.filaService.FilaService;
import com.github.dio.mensageria.listener.ListenerNovaMensagem;
import com.github.dio.mensageria.model.Paciente;
import com.github.dio.mensageria.model.PacienteEncapsuladoNaoRespondido;
import com.github.dio.mensageria.repository.PacienteRepository;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.companion.CompanionDevice;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.signal.auth.Version;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;


/**
 *  Classe de serviço aonde conectamos a biblioteca o Auties/cobalt para inicialização de uma
 *  instãncia do {@link Whatsapp}, além de enviar mensagem.
 * @author diogenesssantos.
 */
@Service
public class WhatsappService {

    private static final Logger log = LoggerFactory.getLogger(com.github.dio.mensageria.service.WhatsappService.class);
    /**
     * Mensagem  padrão para uso de não obtimento de resposta.
     * @hidden
     */
    public static final String NAO_RESPONDIDA_MSG_PADRÃO = "Informamos que o prazo de 48 horas para a retirada do comprovante de agendamento expirou. " +
            "Se você já retirou o seu comprovante, por favor, desconsidere este aviso. Caso contrário, " +
            "seu comprovante será encaminhado para a Unidade Básica de Saúde (Posto de Saúde) do seu " +
            "bairro e estará disponível para retirada em até 24 horas a partir do recebimento desta mensagem.\n\n" +
            "Atenciosamente, Regulação de Saúde.";

    /**
     * Mensagem padrão para uso de não obtimento de resposta.
     * @hidden
     */
    public static final String NAO_RESPONDIDA_MSG_PADRÃO_FINAL_SEMANA = "Informamos que o prazo de 48 horas para a retirada do comprovante de agendamento expirou. " +
            "Se você já retirou o seu comprovante, por favor, desconsidere este aviso. Caso contrário, " +
            "seu comprovante será encaminhado para a Unidade Básica de Saúde (Posto de Saúde) do seu " +
            "bairro e estará disponível para retirada nessa proxima terça feira.\n\n" +
            "Atenciosamente, Regulação de Saúde.";

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final LinkedBlockingQueue<ListenerNovaMensagem> queue = new LinkedBlockingQueue<>(250);
    private static Set<String> pacienteSetStringUUID = new ConcurrentSkipListSet<>();
    private static CompletableFuture<Whatsapp> whatsappFuture;
    /**
     * Uma constante reponsável por armazenar os paciente que não responderam à mensagem.
     */
    public static final List<PacienteEncapsuladoNaoRespondido> pacienteList = new LinkedList<>();


    @Autowired
    private FilaService filaService;
    private String qrCode;
    private Boolean isDisconnecting = true;
    private PacienteRepository pacienteRepository;


    /**
     * Inicialização do  Whatsapp service.
     *
     * @param pacienteRepository o pacienteRepository para persistimos os dados no banco de dados.
     */
    @Autowired
    public WhatsappService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }


    /**
     * Init.
     * após a construção do objeto pelo contexto spring o método chama a biblioteca do autie/cobalt para inicializar
     * uma instância do whatsapp, permitindo a conexão via QR chamado pelo {@link com.github.dio.mensageria.controller.QrCodeController},
     * o método também possui uma prevenção de desconexão do whatsapp para não perdemos os {@link ListenerNovaMensagem}
     * vejá o método recuperandoListenerNovaMensagem(whatsappFuture) e sua documentação.
     */
    @PostConstruct
    public void init() {
        whatsappFuture = new CompletableFuture<>();


        Whatsapp whatsapp = Whatsapp.webBuilder()
                .lastConnection()
                .name("CMCE LOGIN")
                .unregistered(qrCode -> {
                    System.out.println("QRCodeRecebido");
                    this.qrCode = qrCode;

                })
                .addLoggedInListener(api -> {
                    System.out.printf("conectado: %s%n", api.store().privacySettings());
                    isDisconnecting = false;

                })
                .addDisconnectedListener(reason -> {
                    whatsappFuture = new CompletableFuture<>();
                    System.out.printf("disconectado: %s%n", reason);
                });

        whatsapp.store().setDevice(CompanionDevice.web(Version.of("2.3000.1023231279")));
        whatsapp.connect();
        whatsappFuture.complete(whatsapp);
        recuperandoListenerNovaMensagem(whatsappFuture);

    }


    /**
     * Após a construção do objeto chamamos o método limpandoListLinkedWhasappListener(), leia a documentação do método
     * para mais detalhes.
     */
    @PostConstruct
    public void executeThreadLimpezaMemoria() {
        limpandoListLinkedWhatsappListener();
    }

    /**
     * Gets qr code.
     * Esse método em especifico é utilizado pelo {@link com.github.dio.mensageria.controller.QrCodeController}
     * @return o qr code
     * @hidden
     */
    public String getQrCode() {
        return qrCode;
    }


    /**
     * Conectar.
     * Na regra de negócio pode ser chamado pelo end-point,{@link com.github.dio.mensageria.controller.WhatsappController},
     * como também tem o propósito de reconexão do whatsap chamado pelo verificarConexao(), leia a documentação do método.
     */
    public void conectar() {
        init();
    }


    /**
     * Desconectar, na regra de negócio pode ser chamado pelo end-point
     * {@link com.github.dio.mensageria.controller.WhatsappController}, assim permitindo o thread verificarConexao()
     * faça reconexão da instância {@link Whatsapp}.
     */
    public void desconectar() {
        if (isDisconnecting) {
            System.out.println("Desconexão já está em andamento.");
            return;
        }

        try {
            if (whatsappFuture != null && whatsappFuture.get() != null) {
                isDisconnecting = true;
                whatsappFuture.get().disconnect().thenAccept(unused -> {
                    whatsappFuture = new CompletableFuture<>();
                    System.out.println("API DESCONECTADA");
                }).exceptionally(throwable -> {
                    log.warn("ERRO NA API" + throwable);
                    isDisconnecting = false;
                    return null;
                });

            }

        } catch (Exception e) {
            new RuntimeException("ALGUMA ERRO NA INICIALIZAÇÃO");
            e.printStackTrace();
        }
    }


    /**
     * Primero passo.
     * Do {@link com.github.dio.mensageria.controller.WhatsappController} recebemos uma lista de paciente
     * representacional, como esse paciente pode ter uma lista<Numero> chamamos outros método para enviar para cada
     * número desse paciente.
     *
     * @param pacienteMRList the paciente mr list
     */
    public void enviarMensagemLista(List<PacienteMR> pacienteMRList) {
        pacienteMRList.forEach(pacienteMR -> {
            try {
                enviarMensagem(pacienteMR);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * Segundo passo.
     * Do método anterior pegamos cada usuario que possui 1 ou n números e chamamos o
     * enviandoMensagemTexto(PacienteMR pacienteMR, String numero).
     *
     * @param paciente o paciente único retirado da lista recebida pelo {@link com.github.dio.mensageria.controller.WhatsappController}
     * @throws InterruptedException the interrupted exception
     */
    public void enviarMensagem(PacienteMR paciente) throws InterruptedException {
        for (int i = 0; i < paciente.getNumeros().size(); i++) {
            enviandoMensagemTexto(paciente, "55" + paciente.getNumeros().get(i));
            Thread.sleep(10000L);
        }


    }


    /**
     * Terceiro passo.
     * Método responsável para envio da mensagem, e delega responsabilidade para outros métodos
     * salvandoIncialmenteAguardando(PacienteMR pacienteMR);
     * salvandoNaoPossuiWhatsapp(PacienteMR pacienteMR);
     * fazer a persistência no banco de dados, que posteriormente também é criado {@link ListenerNovaMensagem}, ele possui
     * uma própria documentação explicando sua responsabilidade leia.
     *
     * @param pacienteMR
     * @param numero
     */


    private void enviandoMensagemTexto(PacienteMR pacienteMR, String numero) {

        whatsappFuture.thenAccept(whatsapp -> {
            try {
                var contactJid = Jid.of(numero);
                if (whatsapp.hasWhatsapp(contactJid).get()) {
                    var pacientePersistido = salvandoIncialmenteAguardando(pacienteMR);
                    PacienteEncapsuladoNaoRespondido pacienteNaoRespondido = new PacienteEncapsuladoNaoRespondido(pacientePersistido, contactJid);
                    pacienteList.add(pacienteNaoRespondido);

                    var listener = new ListenerNovaMensagem(numero, pacienteRepository, pacientePersistido, queue, pacienteNaoRespondido, filaService);
                    queue.add(listener);
                    whatsapp.addListener(listener);


                    if (!whatsapp.isConnected()) {
                        System.err.println("O WhatsApp não está conectado.");
                        return;
                    }
                    System.out.println("Enviando mensagem para: " + numero);


                    String mensagem = String.format(
                            "Olá %S!%n%n" +
                                    "Somos da Secretaria de Saúde de Vitória de Santo Antão.%n" +
                                    "Temos o prazer de informá-lo sobre a sua consulta ou exame:%n%S.%n%n" +
                                    "Ao receber esta mensagem, por favor, responda com 'SIM' se você tem interesse na consulta ou exame, ou 'NÃO' caso contrário.%n" +
                                    "Informamos que aguardaremos sua resposta por 48 horas.%n%n" +
                                    "Atenciosamente,%n" +
                                    "Regulação de Saúde.",
                            pacienteMR.getNome(), pacienteMR.getConsulta());


                    whatsapp.sendMessage(contactJid, mensagem).thenRun(() -> {
                        System.out.println("Mensagem enviada para: " + numero);

                    }).exceptionally(ex -> {
                        log.warn("Erro ao enviar mensagem: " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
                } else {
                    salvandoNaoPossuiWhatsapp(pacienteMR);
                }
            } catch (Exception e) {
                log.warn("Erro ao enviar mensagem: " + e.getMessage());
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            log.warn("Falha ao obter a instância do WhatsApp: " + ex.getMessage());
            return null;
        });
    }



    private Paciente salvandoIncialmenteAguardando(PacienteMR pacienteMR) {
        var pacienteBdExiste = pacienteRepository.findBycodigo(pacienteMR.getId().toString());
        var paciente = AssemblerPaciente.disassembleToObject(pacienteMR);


        if (pacienteBdExiste == null) {
            pacienteBdExiste = paciente;
            pacienteSetStringUUID.add(pacienteMR.getId().toString());
            return pacienteRepository.save(pacienteBdExiste);
        }


        if (!pacienteSetStringUUID.contains(pacienteMR.getId().toString())) {
            pacienteBdExiste.setMotivo(paciente.getMotivo());
            pacienteBdExiste.setNumero(paciente.getNumero());
            pacienteSetStringUUID.add(pacienteMR.getId().toString());
            return pacienteRepository.save(pacienteBdExiste);
        }

        return pacienteBdExiste;
    }


    private void salvandoNaoPossuiWhatsapp(PacienteMR pacienteMR) {
        var pacienteExiste = pacienteRepository.findBycodigo(pacienteMR.getId().toString());

        if (pacienteExiste == null) {
            var paciente = AssemblerPaciente.disassembleToObjectNaoPossuiWhatsapp(pacienteMR);
            pacienteRepository.save(paciente);
        }
    }

    private void salvandoNaoRespondido(Paciente paciente) {
        var pacienteExiste = pacienteRepository.findBycodigo(paciente.getId().toString());
        if (pacienteExiste == null) {
            paciente.setMotivo("NAO_RESPONDIDO");
            pacienteRepository.save(paciente);

        }
    }


    /**
     * Método para validar final de semana, para uso das constante NAO_RESPONDIDA_MSG_PADRÃO //
     * NAO_RESPONDIDA_MSG_PADRÃO_FINAL_SEMANA
     * @return true ou false
     */
    private static boolean isFinalSemana() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Recife"));
        return data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SATURDAY.toString())
                || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SUNDAY.toString())
                || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.FRIDAY.toString());
    }


    /**
     * Responsabilidade desse método caso uma desconexão inesperada do {@link Whatsapp}, quando o
     * método verificarConexão() reconectar na instãncia do whatsapp os {@link ListenerNovaMensagem} não serem perdidos.
     * @param whatsappFuture recebemos o whastsapp recuperado do método init().
     */
    private void recuperandoListenerNovaMensagem(CompletableFuture<Whatsapp> whatsappFuture) {
        Iterator<ListenerNovaMensagem> iterator = queue.iterator();

        while (iterator.hasNext()) {
            try {
                whatsappFuture.get().addListener(iterator.next());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }


    }


    /**
     * Esse método à cada 30 segundos verifica se o whatsapp está conectado com auxílio de uma thread
     * paralela é criada pelo contexto spring, caso não esteja conectado é chamado o método conectar() que chama
     * posteriormente o init() e temos a reconexão do {@link Whatsapp}, ele também chama explicitamente o
     * GarbageCollection para uma limpeza da instãncia perdida,
     * (sabendo-se que a limpeza não é imediata leia a documentação do JAVA e sobre GARBAGE_COLLECTION).
     *
     * @throws InterruptedException the interrupted exception
     */
    @Scheduled(initialDelay = 120000L, fixedDelay = 30000L)
    public void verificarConexao() throws InterruptedException {
        log.info("LIMPEZA_DO_GARBAGE_COLLECTION");
        System.gc();
        TimeUnit.SECONDS.sleep(15);
        Whatsapp whatsapp = whatsappFuture.getNow(null);

        if (whatsapp == null) {
            log.warn("Instância do WhatsApp ainda não disponível.");
            conectar();
        } else if (!whatsapp.isConnected()) {
            log.warn("WhatsApp DESCONECTADO! É necessário reconectar.");
            conectar();
        }
    }


    /**
     * O método tem como responsabilidade a cada 48 horas, aqueles que não responderem a mensagem receberam outra
     * mensagem que passa uma informação depois removemos os {@link ListenerNovaMensagem} fazendo uma limpeza
     * geral do observadores e objetos salvos em mémoria:
     * queue.clear();
     * pacienteSetStringUUID.clear();
     * pacienteList.clear();
     * ListenerNovaMensagem.pacienteList.clear();
     *
     * Além  de para cada {@link ListenerNovaMensagem} chamamos o método thisReset() Leia a documentação {@link ListenerNovaMensagem},
     * deixando eles elegível para futura passagem da garbage collection fazer a limpeza da mémoria.
     * ExecutorService foi utilizado devido a questões didáticas poderia também ser feito usando
     * @Scheduled do spring.
     */
    public void limpandoListLinkedWhatsappListener() {
        Runnable runnable = () -> {
            if (queue.isEmpty()) {
                log.warn("___FILA LISTENER VAZIA___");
                pacienteSetStringUUID.clear();
                queue.clear();
                pacienteList.clear();
                ListenerNovaMensagem.uuidUnicoUsuarioSet.clear();
                return;
            }

            Iterator<ListenerNovaMensagem> iterator = queue.iterator();
            while (iterator.hasNext()) {
                var observado = iterator.next();
                observado.resetThis();
                whatsappFuture.thenAccept(whatsapp -> whatsapp.removeListener(observado));
            }


            CompletableFuture.runAsync(() -> {
                if (!pacienteList.isEmpty()) {

                    pacienteList.forEach(paciente -> {
                        if (!ListenerNovaMensagem.uuidUnicoUsuarioSet.contains(paciente.getPaciente().getCodigo())) {
                            salvandoNaoRespondido(paciente.getPaciente());
                            try {
                                TimeUnit.SECONDS.sleep(10);

                                if (isFinalSemana()) {
                                    whatsappFuture.get().sendMessage(paciente.getNumero(),
                                            NAO_RESPONDIDA_MSG_PADRÃO_FINAL_SEMANA);
                                    return;
                                }
                                whatsappFuture.get().sendMessage(paciente.getNumero(),
                                        NAO_RESPONDIDA_MSG_PADRÃO);

                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }).thenRun(() -> {
                log.warn("____LIMPEZA_PERIÓDICA____");
                pacienteSetStringUUID.clear();
                queue.clear();
                pacienteList.clear();
                ListenerNovaMensagem.uuidUnicoUsuarioSet.clear();
            });
        };

        scheduledExecutorService.scheduleAtFixedRate(runnable, 12, 48, TimeUnit.HOURS);
    }


}







