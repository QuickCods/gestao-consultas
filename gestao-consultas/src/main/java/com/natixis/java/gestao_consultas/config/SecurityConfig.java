package com.natixis.java.gestao_consultas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
 
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable()) // Desabilita CSRF temporariamente para H2 console, ajuste para produção
//             .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin)) // Permite H2 console em iframe
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/medico/**").hasRole("MEDICO")
//                 .requestMatchers("/paciente/**").hasAnyRole("PACIENTE", "MEDICO")
//                 .requestMatchers("/", "/publico").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .formLogin(withDefaults())
//             .logout(withDefaults());
//         return http.build();
//     }
 
//     @Bean
//     public UserDetailsService userDetailsService() {
//         UserDetails admin = User.withDefaultPasswordEncoder()
//             .username("admin")
//             .password("admin123")
//             .roles("MEDICO")
//             .build();
 
//         UserDetails user = User.withDefaultPasswordEncoder()
//             .username("joao")
//             .password("joao123")
//             .roles("PACIENTE")
//             .build();
 
//         return new InMemoryUserDetailsManager(admin, user);
//     }
// }

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF temporariamente para H2 console, ajuste para produção
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin)) // Permite H2 console em iframe
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/h2-console/**"),
                    AntPathRequestMatcher.antMatcher("/css/**"),
                    AntPathRequestMatcher.antMatcher("/js/**"),
                    AntPathRequestMatcher.antMatcher("/images/**"),
                    AntPathRequestMatcher.antMatcher("/register"),
                    AntPathRequestMatcher.antMatcher("/"),
                    AntPathRequestMatcher.antMatcher("/error")
                ).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/medico/**")).hasRole("MEDICO") // Páginas apenas para MEDICO
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).hasRole("MEDICO")   // <-- ADICIONE A NOVA REGRA AQUI
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/consultas", true) // Redireciona para consultas após login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout") // Redireciona para login com mensagem de logout
                .permitAll()
            )
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}