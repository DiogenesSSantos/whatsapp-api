
package com.github.dio.messageira.service;


import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.infraestruct.filaService.FIlaService;
import com.github.dio.messageira.listener.ListenerNovaMensagem;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.model.PacienteEncapsuladoNaoRespondido;
import com.github.dio.messageira.repository.PacienteRepository;
import it.auties.whatsapp.api.MediaProxySetting;
import it.auties.whatsapp.api.TextPreviewSetting;
import it.auties.whatsapp.api.WebHistoryLength;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.jid.Jid;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;


@Service
public class WhatsappService {

    private static final Logger log = LoggerFactory.getLogger(com.github.dio.messageira.service.WhatsappService.class);
    public static final String NAO_RESPONDIDA_MSG_PADRÃO = "Como não recebemos sua resposta dentro do prazo de 24 horas, " +
            "seu comprovante de agendamento será encaminhado para a Unidade Básica de Saúde (Posto de Saúde) do seu bairro.";

    @Autowired
    private FIlaService filaService;


    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final LinkedBlockingQueue<ListenerNovaMensagem> queue = new LinkedBlockingQueue<>(250);
    private static Set<String> pacienteSetStringUUID = new ConcurrentSkipListSet<>();
    private static CompletableFuture<Whatsapp> whatsappFuture;
    public static final List<PacienteEncapsuladoNaoRespondido> pacienteList = new LinkedList<>();

    private String qrCode;
    private Boolean isDisconnecting = true;
    private PacienteRepository pacienteRepository;


    @Autowired
    public WhatsappService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }


    @PostConstruct
    public void init() {
        whatsappFuture = new CompletableFuture<>();


        Whatsapp.webBuilder()
                .firstConnection()
                .name("CMCE LOGIN NÃO DESCONECTE")
                .mediaProxySetting(MediaProxySetting.NONE)
                .automaticMessageReceipts(false)
                .textPreviewSetting(TextPreviewSetting.DISABLED)
                .historyLength(WebHistoryLength.zero())
                .autodetectListeners(true)
                .unregistered(qrCode -> {
                    System.out.println("QRCodeRecebido");
                    this.qrCode = qrCode;

                })
                .addLoggedInListener(api -> {
                    whatsappFuture.complete(api);
                    System.out.printf("conectado: %s%n", api.store().privacySettings());
                    isDisconnecting = false;

                })
                .addDisconnectedListener(reason -> {
                    whatsappFuture = new CompletableFuture<>();
                    System.out.printf("disconectado: %s%n", reason);
                })
                .connect();


    }

    @PostConstruct
    public void executeThreadLimpezaMemoria() {
        limpandoListLinkedWhatsappListener();
    }


    public void conectar() {
        init();
    }


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
            new RuntimeException("ALGUMA ERRO NA APLICAÇÃO");
        }
    }

    public String getQrCode() {
        return qrCode;
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


                    String mensagem1 = String.format(
                            "Olá %S , somos da Secretária de saúde de Vitoria de Santo Antão.%n%n" +
                                    "Estamos felizes em informá-lo sobre sua consulta ou exame: %n%S.%n%n" +
                                    "Gostaríamos de saber se você ainda tem interesse.%n%n" +
                                    "Por favor, responda SIM se estiver interessado, ou NÃO se não estiver. Caso não possua interesse, pedimos gentilmente que forneça sua justificativa..%n%n" +
                                    "Aguardamos sua resposta.%nAtenciosamente, Regulação de saúde."
                            , pacienteMR.getNome(), pacienteMR.getConsulta());

                    whatsapp.sendMessage(contactJid, mensagem1).thenRun(() -> {
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
        var paciente = disassembleToObject(pacienteMR);


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
            var paciente = disassembleToObjectNaoPossuiWhatsapp(pacienteMR);
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


    /*
        Método para converte o modelo representacional (Input de dados)
        no entity que vai ser persistido no banco de dados
     */
    private static Paciente disassembleToObject(PacienteMR pacienteMR) {
        var paciente = new Paciente();
        paciente.setCodigo(pacienteMR.getId().toString());
        paciente.setNome(pacienteMR.getNome());
        paciente.setNumero(pacienteMR.getNumeros().getFirst());
        paciente.setConsulta(pacienteMR.getConsulta());
        paciente.setDataConsulta(pacienteMR.getData());
        paciente.setMotivo("AGUARDANDO");
        paciente.setBairro(pacienteMR.getBairro());
        return paciente;
    }

    private static Paciente disassembleToObjectNaoPossuiWhatsapp(PacienteMR pacienteMR) {
        var paciente = new Paciente();
        paciente.setCodigo(pacienteMR.getId().toString());
        paciente.setNome(pacienteMR.getNome());
        paciente.setNumero("NUMERO NAO EXISTE WHATSAPP");
        paciente.setConsulta(pacienteMR.getConsulta());
        paciente.setDataConsulta(pacienteMR.getData());
        paciente.setMotivo("Nao_Possui_Whatsapp");
        paciente.setBairro(pacienteMR.getBairro());
        return paciente;
    }


    @Scheduled(fixedRate = 45000)
    public void verificarConexao() throws InterruptedException {
        System.gc();
        TimeUnit.SECONDS.sleep(15);
        Whatsapp whatsapp = whatsappFuture.getNow(null);

        if (whatsapp == null) {
            System.out.println("Instância do WhatsApp ainda não disponível.");
            conectar();
        } else if (!whatsapp.isConnected()) {
            System.err.println("WhatsApp DESCONECTADO! É necessário reconectar.");
            conectar();
        }
    }


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
                                whatsappFuture.get().sendMessage(paciente.getNumero(),
                                        NAO_RESPONDIDA_MSG_PADRÃO);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
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



        scheduledExecutorService.scheduleAtFixedRate(runnable, 12, 24, TimeUnit.HOURS);
    }
}







