package com.natixis.java.gestao_consultas.controller;

import com.natixis.java.gestao_consultas.model.Consulta;
import com.natixis.java.gestao_consultas.model.Consulta.EstadoConsulta;
import com.natixis.java.gestao_consultas.model.User;
import com.natixis.java.gestao_consultas.repository.ConsultaRepository;
import com.natixis.java.gestao_consultas.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaRepository consultaRepository;
    private final UserRepository userRepository;

    public ConsultaController(ConsultaRepository consultaRepository, UserRepository userRepository) {
        this.consultaRepository = consultaRepository;
        this.userRepository = userRepository;
    }

    // Página principal de consultas (médico vê tudo, paciente vê só as suas)
    @GetMapping
    public String listarConsultas(
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String estado,
            Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        boolean isMedico = user.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals("ROLE_MEDICO"));

        List<Consulta> consultas;

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(LocalTime.MAX) : null;

        if (isMedico) {
            if (inicio != null && fim != null && estado != null && !estado.isBlank()) {
                consultas = consultaRepository.findByDataHoraBetweenAndEstado(inicio, fim,
                        EstadoConsulta.valueOf(estado));
            } else if (inicio != null && fim != null) {
                consultas = consultaRepository.findByDataHoraBetween(inicio, fim);
            } else if (estado != null && !estado.isBlank()) {
                consultas = consultaRepository.findByEstado(EstadoConsulta.valueOf(estado));
            } else {
                consultas = consultaRepository.findAll();
            }
        } else {
            if (inicio != null && fim != null && estado != null && !estado.isBlank()) {
                consultas = consultaRepository.findByPacienteAndDataHoraBetweenAndEstado(user, inicio, fim,
                        EstadoConsulta.valueOf(estado));
            } else if (inicio != null && fim != null) {
                consultas = consultaRepository.findByPacienteAndDataHoraBetween(user, inicio, fim);
            } else if (estado != null && !estado.isBlank()) {
                consultas = consultaRepository.findByPacienteAndEstado(user, EstadoConsulta.valueOf(estado));
            } else {
                consultas = consultaRepository.findByPaciente(user);
            }
        }

        model.addAttribute("consultas", consultas);
        model.addAttribute("isMedico", isMedico);
        model.addAttribute("user", user);
        model.addAttribute("dataInicio", dataInicio != null ? dataInicio.toString() : "");
        model.addAttribute("dataFim", dataFim != null ? dataFim.toString() : "");

        return "consultas-list"; // único HTML usado
    }

    // Marcar nova consulta (paciente)
    @PostMapping("/nova")
    public String marcarConsulta(@ModelAttribute Consulta consulta, Authentication auth) {
        User paciente = userRepository.findByUsername(auth.getName()).orElseThrow();
        User medico = userRepository.findById(consulta.getMedico().getId()).orElseThrow();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setEstado(EstadoConsulta.PENDENTE);
        consultaRepository.save(consulta);
        return "redirect:/consultas";
    }

    @GetMapping("/nova")
    public String mostrarFormularioNovaConsulta(Model model) {
        model.addAttribute("consulta", new Consulta()); // Para preencher o formulário se necessário
        // Carregar os médicos
        // Buscar diretamente todos os médicos
        List<User> medicos = userRepository.findByRoles_Name("ROLE_MEDICO");

        model.addAttribute("medicos", medicos);
        return "nova"; // Nome do HTML em templates/
    }

    // Cancelar consulta (paciente)
    @PostMapping("/{id}/cancelar")
    public String cancelarConsulta(@PathVariable Long id, Authentication auth) {
        Consulta consulta = consultaRepository.findById(id).orElseThrow();
        User paciente = userRepository.findByUsername(auth.getName()).orElseThrow();

        if (consulta.getPaciente().equals(paciente)) {
            consulta.setEstado(EstadoConsulta.CANCELADA);
            consultaRepository.save(consulta);
        }
        return "redirect:/consultas";
    }

    // Alterar estado (médico)
    @PostMapping("/{id}/alterar-estado")
    public String alterarEstadoConsulta(@PathVariable Long id, @RequestParam EstadoConsulta estado) {
        Consulta consulta = consultaRepository.findById(id).orElseThrow();
        consulta.setEstado(estado);
        consultaRepository.save(consulta);
        return "redirect:/consultas";
    }

    // Agendamento interno com validação
    @Transactional
    public Consulta agendarConsulta(Consulta consulta) {
        validarConsulta(consulta);
        return consultaRepository.save(consulta);
    }

    private void validarConsulta(Consulta consulta) {
        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar consultas no passado");
        }
    }
}