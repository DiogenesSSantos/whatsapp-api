package com.github.dio.messageira;

import it.auties.whatsapp.api.PairingCodeHandler;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.contact.Contact;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.mobile.VerificationCodeMethod;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class WhatsappApiApplication {

    private static CompletableFuture<Whatsapp> whatsappFuture;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(WhatsappApiApplication.class, args);
        System.out.println("Test Commit");
    }

}