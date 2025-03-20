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

    private static final LinkedBlockingQueue<Runnable> fila = new LinkedBlockingQueue<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public FIlaService() {;
    }

    @Autowired
    public FIlaService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public void setPacienteRepository(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    private CompletableFuture<Void> persistirDados(String mensagemUsuario, Paciente paciente) {
        return CompletableFuture.runAsync(() -> {
            if (mensagemUsuario.equalsIgnoreCase("ACEITO")) {
                paciente.setMotivo("ACEITO");
                pacienteRepository.save(paciente);

                return;
            }
            paciente.setMotivo(mensagemUsuario);
            pacienteRepository.save(paciente);


        });

    }


    public void excutarPersistencia (String mensagem, Paciente paciente) {
        //Produces da threads, preparando a fila
        fila.add(()-> {
           try {
                persistirDados(mensagem,paciente).get();
           }catch (Exception exception) {
               exception.printStackTrace();
           }
        });


        //Consume a fila para executar a persistencia de dados assincronamente.
        if (!executorService.isShutdown()) {

            executorService.submit(()-> {
                while (!fila.isEmpty()) {
                    try {
                        Runnable consumo = fila.take();
                        consumo.run();
                    }catch (InterruptedException exception) {
                        exception.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

    }






}
