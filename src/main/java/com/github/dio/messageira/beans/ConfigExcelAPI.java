package com.github.dio.messageira.beans;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component

public class ConfigExcelAPI {


    @Bean
    public CompletableFuture<?> configExcelApi() {

        String teste = "Teste do completableFuture";

        return CompletableFuture.completedFuture(teste);
    }

}
