package com.github.dio.mensageria.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Configuração de CORS.
 * CORS -> é um mecanismo de segurança implementado nos navegadores que decide, por meio de cabeçalhos HTTP,
 * como e se um recurso disponível em um domínio pode ser solicitado por páginas de outro domínio.
 * @hidden
 * @author diogenes
 *
 */
@Configuration
public class WebConfig {
    /**
     * Bean de configuração quais permitimos os métodos de requisições que usuário tem acesso.
     * GET, POST, PUT, DELETE, OPTION.

     * @return  web mvc configurer;
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }
}

