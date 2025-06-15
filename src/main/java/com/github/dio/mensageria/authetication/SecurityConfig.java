package com.github.dio.mensageria.authetication;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe para configuração do spring-security leia a documentação spring-security para mais entendimento dessa classe
 * de configuração.
 * @hidden
 * @author diogenessantos
 * <a href="https://docs.spring.io/spring-security/reference/servlet/configuration/java.html"> Spring-documentação</a>
 */
@Configuration
public class SecurityConfig {

    /**
     * securityFilterChain é o bean para configuração de segurança da nossa classe, aonde configuramos
     * rotas disponiveis sem autenticação, habilitamos também o formulario de login do spring padrão
     * além de desabilitar crsf para o funcionamento do form de login.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/apidocs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/zap/enviarList").authenticated()
                        .anyRequest().authenticated()
                ).csrf(csrf -> csrf.disable())
                .formLogin(httpSecurityFormLoginConfigurer ->
                        httpSecurityFormLoginConfigurer.defaultSuccessUrl("/" , true));

        return http.build();
    }


}
