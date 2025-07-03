package com.natixis.java.gestao_consultas.controller;

import com.natixis.java.gestao_consultas.model.Role; // Importação ajustada
import com.natixis.java.gestao_consultas.model.User; // Importação ajustada
import com.natixis.java.gestao_consultas.repository.RoleRepository; // Importação ajustada
import com.natixis.java.gestao_consultas.repository.UserRepository; // Importação ajustada
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Nome do template Thymeleaf
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("usernameError", "Nome de usuário já existe!");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_PACIENTE")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_PACIENTE"))); // Garante que a role exista
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/consultas-list")
    public String consultasList() {
        return "consultas-list"; // Nome do template Thymeleaf
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied"; // Template de acesso negado
    }
}