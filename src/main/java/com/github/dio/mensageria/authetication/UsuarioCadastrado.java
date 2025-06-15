package com.github.dio.mensageria.authetication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * A classe que auxilia o spring-security se informar os usuários autorizado para uso da aplicação.
 * a configuração está em {@link InMemoryUserDetailsManager } pelo motivo em que a nossa apliacação
 * vai ter um limite de usuários que pode acessar à aplicação,
 * mas nada impede de ser configurado em banco de dados(um princípio de boas práticas).
 * @hidden
 * @author diogenesssantos
 *
 *
 * Consulte também:
 * <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/in-memory.html">Spring Security In-Memory</a>
 */
@Configuration
@EnableWebSecurity
public class UsuarioCadastrado {

    /**
     * Configuramos os usuário permitido para o uso da aplicação.
     * password({noop} -> caso seu password tenha algum script de criptografia é só injetar @Bean e usar seu método()).
     *
     * @return  user configurado.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.builder().username("Seu_Nome_Usuário")
                .password("{noop}Sua_Senha")
                .roles("ADMIM" , "USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }


}
