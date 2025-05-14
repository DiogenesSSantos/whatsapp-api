package com.github.dio.messageira.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(new String[]{"http://localhost:8080", "http://devdiogenes.shop"})
                        .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"});
            }
        };
    }
}


