package com.github.dio.mensageria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutionException;

/**
 * The type Whatsapp api application.
 */
@EnableScheduling
@SpringBootApplication
public class WhatsappApiApplication {

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Erro não capturado na thread " + thread.getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        });
    }


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(WhatsappApiApplication.class, args);
        System.out.println("Test Commit");
    }

}