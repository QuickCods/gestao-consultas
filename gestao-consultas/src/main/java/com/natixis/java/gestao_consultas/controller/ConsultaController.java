package com.natixis.java.gestao_consultas.controller;

import com.natixis.java.gestao_consultas.model.Consulta;
import com.natixis.java.gestao_consultas.model.Consulta.EstadoConsulta;
import com.natixis.java.gestao_consultas.model.Role;
import com.natixis.java.gestao_consultas.model.User;
import com.natixis.java.gestao_consultas.repository.ConsultaRepository;
import com.natixis.java.gestao_consultas.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public String listarConsultas(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        boolean isMedico = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_MEDICO"));

        List<Consulta> consultas = isMedico
                ? consultaRepository.findAll()
                : consultaRepository.findByPaciente(user);

        // debug
        System.out.println("Consultas encontradas para " + user.getUsername() + ": " + consultas.size());
        consultas.forEach(c -> System.out.println("Consulta ID: " + c.getId() + ", Paciente: " + c.getPaciente().getUsername()));

        model.addAttribute("consultas", consultas);
        model.addAttribute("isMedico", isMedico);
        model.addAttribute("user", user);

        return "consultas-list"; // único HTML usado
    }

    // Marcar nova consulta (paciente)
    @PostMapping("/nova")
    public String marcarConsulta(@ModelAttribute Consulta consulta, Authentication auth) {
        //debug
        System.out.println("POST /consultas/nova recebido, consulta: " + consulta);
        User paciente = userRepository.findByUsername(auth.getName()).orElseThrow();
        User medico = userRepository.findById(consulta.getMedico().getId()).orElseThrow();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setEstado(EstadoConsulta.PENDENTE);
        consultaRepository.save(consulta);
        //debug
        System.out.println("Consulta salva: " + consulta.getId() + ", paciente: " + consulta.getPaciente().getUsername());
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

// boolean conflito = consultaRepository.existsByMedicoAndDataHoraBetween(
// consulta.getMedico(),
// consulta.getDataHora().minusMinutes(30),
// consulta.getDataHora().plusMinutes(30)
// );

// if (conflito) {
// throw new IllegalStateException("Conflito de agendamento");
// }

// Outros métodos...

// public List<Consulta> listarConsultasPorMedico(User medico) {
// return consultaRepository.findByMedico(medico);
// }

// public List<Consulta> listarConsultasPorPaciente(User paciente) {
// return consultaRepository.findByPaciente(paciente);
// }

// public List<Consulta> listarConsultasPorData(LocalDateTime data) {
// return consultaRepository.findByDataHora(data);
// }
// public List<Consulta> listarConsultasPorDataIntervalo(LocalDateTime
// dataInicial, LocalDateTime dataFinal) {
// return consultaRepository.findByDataHoraBetween(dataInicial, dataFinal);
// }
// public List<Consulta> listarConsultasPorEstado(EstadoConsulta estado) {
// return consultaRepository.findAllByEstado(estado);
// }
// public List<Consulta> listarConsultasPorDescricao(String descricao) {
// return consultaRepository.findAllByDescricaoContaining(descricao);
// }
// public List<Consulta> listarConsultasPorMedicoDataHora(User medico,
// LocalDateTime dataHora) {
// return consultaRepository.findAllByMedicoAndDataHora(medico, dataHora);
// }
// public List<Consulta> listarConsultasPorPacienteDataHora(User paciente,
// LocalDateTime dataHora) {
// return consultaRepository.findAllByPacienteAndDataHora(paciente, dataHora);
// }
// public List<Consulta> listarConsultasPorMedicoDataHoraIntervalo(User medico,
// LocalDateTime dataHoraInicial, LocalDateTime dataHoraFinal) {
// return consultaRepository.findAllByMedicoAndDataHoraBetween(medico,
// dataHoraInicial, dataHoraFinal);
// }

}