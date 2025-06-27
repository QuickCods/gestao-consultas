package com.natixis.java.gestao_consultas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
 
import static org.springframework.security.config.Customizer.withDefaults;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
 
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//         AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
//         accessDeniedHandler.setErrorPage("/erro");
        
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
//                 .requestMatchers("/medico/**").hasRole("MEDICO")
//                 .requestMatchers("/paciente/**").hasRole("PACIENTE")
//                 .anyRequest().authenticated()
//             )
//             .formLogin(form -> form
//                 .loginPage("/login")
//                 .defaultSuccessUrl("/", true)
//                 .permitAll()
//             )
//             .logout(logout -> logout
//                 .logoutSuccessUrl("/login?logout")
//                 .permitAll()
//             )
//             .exceptionHandling(handler -> handler
//                 .accessDeniedHandler(accessDeniedHandler)
//             );
//         return http.build();
//     }
 
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }

@Configuration
@EnableWebSecurity
public class SecurityConfig {
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/medico/**").hasRole("MEDICO")
                .requestMatchers("/paciente/**").hasAnyRole("PACIENTE", "MEDICO")
                .requestMatchers("/", "/publico").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(withDefaults())
            .logout(withDefaults());
        return http.build();
    }
 
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin123")
            .roles("MEDICO")
            .build();
 
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("joao")
            .password("joao123")
            .roles("PACIENTE")
            .build();
 
        return new InMemoryUserDetailsManager(admin, user);
    }
}