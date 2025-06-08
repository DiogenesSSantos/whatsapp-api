package com.github.dio.mensageria.authetication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author diogenesssantos
 *
 * A classe que auxilia o spring-security se informar os usuários autorizado para uso da aplicação.
 * a configuração está em {@link InMemoryUserDetailsManager } pelo motivo em que a nossa apliacação
 * vai ter um limite de usuários que pode acessar a aplicação,
 * mas nada impede de ser configurado em banco de dados(um princípio de boas práticas).
 *
 * @see @{@link "https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/in-memory.html"}
 */
@Configuration
@EnableWebSecurity
public class UsuarioCadastrado {

    /**
     * Configuramos os usuário permitido para o uso da aplicação.
     * password({noop} -> caso seu password tenha algum script de criptografia e śo injetar @Bean e usar seu método).
     *
     * @return  user configurado.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.builder().username("DioDev")
                .password("{noop}Dio84768748@")
                .roles("ADMIM")
                .build();

        return new InMemoryUserDetailsManager(user);
    }


}
