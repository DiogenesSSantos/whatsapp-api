package com.github.dio.messageira;

import it.auties.whatsapp.api.Whatsapp;
import net.sf.jasperreports.components.table.fill.TableJasperReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@EnableScheduling
@SpringBootApplication
public class WhatsappApiApplication {

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Erro não capturado na thread " + thread.getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        });
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(WhatsappApiApplication.class, args);
        System.out.println("Test Commit");
    }

}