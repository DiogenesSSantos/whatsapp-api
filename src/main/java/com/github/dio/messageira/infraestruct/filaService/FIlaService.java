package com.github.dio.messageira.infraestruct.filaService;

import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class FIlaService {
    @Autowired
    private PacienteRepository pacienteRepository;
    private static final LinkedBlockingQueue<Runnable> fila = new LinkedBlockingQueue();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FIlaService() {
    }

    @Autowired
    public FIlaService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

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

    public void excutarPersistencia(String mensagem, Paciente paciente, String numero) {
        fila.add((Runnable)() -> {
            try {
                this.persistirDados(mensagem, paciente , numero.substring(3)).get();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });
        if (!this.executorService.isShutdown()) {
            this.executorService.submit(() -> {
                while(!fila.isEmpty()) {
                    try {
                        Runnable consumo = (Runnable)fila.take();
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
