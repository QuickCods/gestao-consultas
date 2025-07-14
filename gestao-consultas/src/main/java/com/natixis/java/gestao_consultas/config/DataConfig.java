package com.natixis.java.gestao_consultas.config;

import com.natixis.java.gestao_consultas.model.*;
import com.natixis.java.gestao_consultas.model.Consulta.EstadoConsulta;
import com.natixis.java.gestao_consultas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataConfig {

        private final PasswordEncoder passwordEncoder;

        public DataConfig(PasswordEncoder passwordEncoder) {
                this.passwordEncoder = passwordEncoder;
        }

        @Bean
        public CommandLineRunner initData(
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        ConsultaRepository consultaRepository) {
                return args -> {
                        // 1. CRIAR ROLES
                        Role adminRole = roleRepository.findByName("ROLE_MEDICO")
                                        .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_MEDICO")));
                        Role userRole = roleRepository.findByName("ROLE_PACIENTE")
                                        .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_PACIENTE")));

                        // 2. CRIAR USUÁRIOS USANDO SETTERS (MÉTODO CORRIGIDO E MAIS ROBUSTO)
                        User jacintoLeite = userRepository.findByUsername("jacintoLeite")
                                        .orElseGet(() -> {
                                                User newUser = new User();
                                                newUser.setUsername("jacintoLeite");
                                                newUser.setPassword(passwordEncoder.encode("medic0123"));
                                                newUser.setEmail("medico@example.com");
                                                newUser.setRoles(Set.of(adminRole, userRole));
                                                return userRepository.save(newUser);
                                        });

                        User anaCacho = userRepository.findByUsername("anaCacho")
                                        .orElseGet(() -> {
                                                User newUser = new User();
                                                newUser.setUsername("anaCacho");
                                                newUser.setPassword(passwordEncoder.encode("ana123"));
                                                newUser.setEmail("ana.cacho.paulo@example.com");
                                                newUser.setRoles(Set.of(userRole));
                                                return userRepository.save(newUser);
                                        });

                        User bruno = userRepository.findByUsername("bruno")
                                        .orElseGet(() -> {
                                                User newUser = new User();
                                                newUser.setUsername("bruno");
                                                newUser.setPassword(passwordEncoder.encode("bruno"));
                                                newUser.setEmail("bruno.costa@example.com");
                                                newUser.setRoles(Set.of(userRole));
                                                return userRepository.save(newUser);
                                        });

                        User mikeOxlong = userRepository.findByUsername("mikeOxlong")
                                        .orElseGet(() -> {
                                                User newUser = new User();
                                                newUser.setUsername("mikeOxlong");
                                                newUser.setPassword(passwordEncoder.encode("mikeOxlong"));
                                                newUser.setEmail("mikeOxlong.costa@example.com");
                                                newUser.setRoles(Set.of(userRole));
                                                return userRepository.save(newUser);
                                        });

                        User draCatarina = userRepository.findByUsername("draCatarina")
                                        .orElseGet(() -> {
                                                User newUser = new User();
                                                newUser.setUsername("draCatarina");
                                                newUser.setPassword(passwordEncoder.encode("catarina123"));
                                                newUser.setEmail("catarina.silva@example.com");
                                                newUser.setRoles(Set.of(adminRole, userRole));
                                                return userRepository.save(newUser);
                                        });

                        // Novo médico 2: drHenrique
                        User drHenrique = userRepository.findByUsername("drHenrique")
                                        .orElseGet(() -> {
                                                User newUser = new User();
                                                newUser.setUsername("drHenrique");
                                                newUser.setPassword(passwordEncoder.encode("henrique123"));
                                                newUser.setEmail("henrique.oliveira@example.com");
                                                newUser.setRoles(Set.of(adminRole, userRole));
                                                return userRepository.save(newUser);
                                        });

                        // 3. CRIAR CONSULTAS

                        Consulta c1 = new Consulta(bruno, jacintoLeite,
                                        LocalDateTime.of(2025, 9, 6, 4, 20),
                                        EstadoConsulta.PENDENTE,
                                        "Torcao testicular");
                        c1.setPaciente(bruno);
                        consultaRepository.save(c1);
                        System.out.println("Consulta 1 criada: " + c1.getId());

                        Consulta c2 = new Consulta(anaCacho, jacintoLeite,
                                        LocalDateTime.of(2025, 3, 11, 18, 15),
                                        EstadoConsulta.CONCLUIDA,
                                        "Papa nicolau");
                        c2.setPaciente(anaCacho);
                        consultaRepository.save(c2);

                        Consulta c3 = new Consulta(mikeOxlong, jacintoLeite,
                                        LocalDateTime.of(2025, 6, 9, 9, 15),
                                        EstadoConsulta.CANCELADA,
                                        "Nao sabe dizer a letra X");
                        c3.setPaciente(mikeOxlong);
                        consultaRepository.save(c3);

                        Consulta c4 = new Consulta(bruno, draCatarina,
                                        LocalDateTime.of(2025, 10, 5, 14, 30),
                                        EstadoConsulta.PENDENTE,
                                        "Alergia inexplicável na pele");
                        c4.setPaciente(bruno);
                        consultaRepository.save(c4);

                        Consulta c5 = new Consulta(anaCacho, draCatarina,
                                        LocalDateTime.of(2025, 11, 20, 9, 45),
                                        EstadoConsulta.CONCLUIDA,
                                        "Consulta ginecológica de rotina");
                        c5.setPaciente(anaCacho);
                        consultaRepository.save(c5);

                        Consulta c6 = new Consulta(mikeOxlong, drHenrique,
                                        LocalDateTime.of(2025, 8, 12, 16, 00),
                                        EstadoConsulta.PENDENTE,
                                        "Consulta psicológica - stress");
                        c6.setPaciente(mikeOxlong);
                        consultaRepository.save(c6);

                        Consulta c7 = new Consulta(bruno, drHenrique,
                                        LocalDateTime.of(2025, 12, 1, 11, 15),
                                        EstadoConsulta.CANCELADA,
                                        "Consulta de nutrição cancelada");
                        c7.setPaciente(bruno);
                        consultaRepository.save(c7);

                };
        }
}