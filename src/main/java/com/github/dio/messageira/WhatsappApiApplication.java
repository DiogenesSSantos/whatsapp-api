package com.github.dio.messageira;

import it.auties.whatsapp.api.Whatsapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class WhatsappApiApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(WhatsappApiApplication.class, args);
        System.out.println("Test Commit");
    }

}