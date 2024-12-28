package com.github.dio.messageira.listener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class AsyncListenerNovaMensagem {

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster () {
        var simple = new SimpleApplicationEventMulticaster();
        simple.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return simple;
    }


}
