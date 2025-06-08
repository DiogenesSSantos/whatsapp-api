package com.github.dio.mensageria.infraestrutura.filaService;

import com.github.dio.mensageria.model.Paciente;
import com.github.dio.mensageria.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The type Fila service.
 */
@Service
public class FilaService {
    private static final LinkedBlockingQueue<Runnable> fila = new LinkedBlockingQueue(250);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private PacienteRepository pacienteRepository;



    private CompletableFuture<Void> persistirDados(String mensagemUsuario, Paciente paciente, String numero) {
        return CompletableFuture.runAsync(() -> {
            if (mensagemUsuario.equalsIgnoreCase("ACEITO")) {
                paciente.setMotivo("ACEITO");
                paciente.setNumero(numero);
                this.pacienteRepository.save(paciente);
            } else {
                paciente.setMotivo(mensagemUsuario);
                paciente.setNumero(numero);
                this.pacienteRepository.save(paciente);
            }
        });
    }

    /**
     * Executa producao e consumo.
     *
     * @param mensagem the mensagem
     * @param paciente the paciente
     * @param numero   the numero
     */
    public void executaProducaoEConsumo(String mensagem, Paciente paciente, String numero) {

        fila.add(() -> {
            try {
                this.persistirDados(mensagem, paciente, numero.substring(3)).get();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });


        if (!this.executorService.isShutdown()) {
            this.executorService.submit(() -> {
                while (!fila.isEmpty()) {
                    try {
                        Runnable consumo = fila.take();
                        consumo.run();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }

            });
        }

    }
}
