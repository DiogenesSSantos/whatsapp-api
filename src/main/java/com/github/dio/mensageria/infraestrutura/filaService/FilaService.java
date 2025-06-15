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
 * A classe que possui à metodologia de PRODUCE AND CONSUMER.
 * usando {@link ExecutorService } para executar em uma SingleThead a persistência de dados.
 * @author diogenesssantos.
 *
 */
@Service
public class FilaService {
    private static final LinkedBlockingQueue<Runnable> fila = new LinkedBlockingQueue(250);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private PacienteRepository pacienteRepository;


    /**
     * Recebemos todos os parâmetros do método executarProducaoEConsumo,
     *
     * @return task {@link CompletableFuture} que possui uma regra de negócio aonde fazendo a
     * persistência usando Spring-jpa.
     */
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
     * Executa um método persistirDados(um {@link CompletableFuture}) que possui  á regra de negocio aonde e feito a
     * persistência no banco de dados.
     * além de possuir o padrão FIFO, Usando fila {@link LinkedBlockingQueue}.
     *
     * @param mensagem a mensagem do usuário,
     * @param paciente os dados paciente,
     * @param numero    o numero do whatsapp,
     *                 devido que o mesmo pode ter n contatos, persistimos apenas a resposta do primero.
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
