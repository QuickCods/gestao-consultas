package com.natixis.java.gestao_consultas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF temporariamente para H2 console, ajuste para produção
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Permite H2
                                                                                                     // console em
                                                                                                     // iframe
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/h2-console/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/register",
                                "/",
                                "/error")
                        .permitAll()
                        .requestMatchers("/medico/**").hasRole("MEDICO") // Páginas apenas para MEDICO
                        .requestMatchers("/api/**").hasRole("MEDICO") // Regra nova para API
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/consultas", true) // Redireciona para consultas após login
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout") // Configuração recomendada para logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied") // Página de acesso negado
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}