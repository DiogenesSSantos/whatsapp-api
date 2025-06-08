
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
 * The type Whatsapp service.
 *
 * @author Diogenes_Santos
 */
@Service
public class WhatsappService {

    private static final Logger log = LoggerFactory.getLogger(com.github.dio.mensageria.service.WhatsappService.class);
    /**
     * The constant NAO_RESPONDIDA_MSG_PADRÃO.
     */
    public static final String NAO_RESPONDIDA_MSG_PADRÃO = "Informamos que o prazo de 48 horas para a retirada do comprovante de agendamento expirou. " +
            "Se você já retirou o seu comprovante, por favor, desconsidere este aviso. Caso contrário, " +
            "seu comprovante será encaminhado para a Unidade Básica de Saúde (Posto de Saúde) do seu " +
            "bairro e estará disponível para retirada em até 24 horas a partir do recebimento desta mensagem.\n\n" +
            "Atenciosamente, Regulação de Saúde.";

    /**
     * The constant NAO_RESPONDIDA_MSG_PADRÃO_FINAL_SEMANA.
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
     * The constant pacienteList.
     */
    public static final List<PacienteEncapsuladoNaoRespondido> pacienteList = new LinkedList<>();


    @Autowired
    private FilaService filaService;
    private String qrCode;
    private Boolean isDisconnecting = true;
    private PacienteRepository pacienteRepository;


    /**
     * Instantiates a new Whatsapp service.
     *
     * @param pacienteRepository the paciente repository
     */
    @Autowired
    public WhatsappService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }


    /**
     * Init.
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
     * Execute thread limpeza memoria.
     */
    @PostConstruct
    public void executeThreadLimpezaMemoria() {
        limpandoListLinkedWhatsappListener();
    }

    /**
     * Gets qr code.
     *
     * @return the qr code
     */
    public String getQrCode() {
        return qrCode;
    }


    /**
     * Conectar.
     */
    public void conectar() {
        init();
    }


    /**
     * Desconectar.
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
                    System.out.println("ERRO NA API" + throwable);
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
     * Enviar mensagem lista.
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
     * Enviar mensagem.
     *
     * @param paciente the paciente
     * @throws InterruptedException the interrupted exception
     */
    public void enviarMensagem(PacienteMR paciente) throws InterruptedException {
        for (int i = 0; i < paciente.getNumeros().size(); i++) {
            enviandoMensagemTexto(paciente, "55" + paciente.getNumeros().get(i));
            Thread.sleep(10000L);
        }


    }


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
                        System.err.println("Erro ao enviar mensagem: " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
                } else {
                    salvandoNaoPossuiWhatsapp(pacienteMR);
                }
            } catch (Exception e) {
                System.err.println("Erro ao enviar mensagem: " + e.getMessage());
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            System.err.println("Falha ao obter a instância do WhatsApp: " + ex.getMessage());
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


    private static boolean isFinalSemana() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Recife"));
        return data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SATURDAY.toString())
                || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.SUNDAY.toString())
                || data.getDayOfWeek().toString().equalsIgnoreCase(DayOfWeek.FRIDAY.toString());
    }


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
     * Verificar conexao.
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
     * Limpando list linked whatsapp listener.
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
                log.warn("____LIMPEZA PERIÓDICA____");
                pacienteSetStringUUID.clear();
                queue.clear();
                pacienteList.clear();
                ListenerNovaMensagem.uuidUnicoUsuarioSet.clear();
            });
        };

        scheduledExecutorService.scheduleAtFixedRate(runnable, 12, 48, TimeUnit.HOURS);
    }


}







