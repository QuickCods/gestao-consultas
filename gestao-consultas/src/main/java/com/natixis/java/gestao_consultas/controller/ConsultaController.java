package com.natixis.java.gestao_consultas.controller;

import com.natixis.java.gestao_consultas.model.*;
import com.natixis.java.gestao_consultas.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaController {
    
    private final ConsultaRepository consultaRepository;
    
    public ConsultaController(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }
    
    @Transactional
    public Consulta agendarConsulta(Consulta consulta) {
        validarConsulta(consulta);
        return consultaRepository.save(consulta);
    }
    
    private void validarConsulta(Consulta consulta) {
        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar consultas no passado");
        }
        
        // boolean conflito = consultaRepository.existsByMedicoAndDataHoraBetween(
        //     consulta.getMedico(),
        //     consulta.getDataHora().minusMinutes(30),
        //     consulta.getDataHora().plusMinutes(30)
        // );
        
        // if (conflito) {
        //     throw new IllegalStateException("Conflito de agendamento");
        // }
    }
    
    // Outros métodos...


    // public List<Consulta> listarConsultasPorMedico(User medico) {
    //     return consultaRepository.findByMedico(medico);
    // }

    // public List<Consulta> listarConsultasPorPaciente(User paciente) {
    //     return consultaRepository.findByPaciente(paciente);
    // }


    // public List<Consulta> listarConsultasPorData(LocalDateTime data) {
    //     return consultaRepository.findByDataHora(data);
    // }
    // public List<Consulta> listarConsultasPorDataIntervalo(LocalDateTime dataInicial, LocalDateTime dataFinal) {
    //     return consultaRepository.findByDataHoraBetween(dataInicial, dataFinal);
    // }
    // public List<Consulta> listarConsultasPorEstado(EstadoConsulta estado) {
    //     return consultaRepository.findAllByEstado(estado);
    // }
    // public List<Consulta> listarConsultasPorDescricao(String descricao) {
    //     return consultaRepository.findAllByDescricaoContaining(descricao);
    // }
    // public List<Consulta> listarConsultasPorMedicoDataHora(User medico, LocalDateTime dataHora) {
    //     return consultaRepository.findAllByMedicoAndDataHora(medico, dataHora);
    // }
    // public List<Consulta> listarConsultasPorPacienteDataHora(User paciente, LocalDateTime dataHora) {
    //     return consultaRepository.findAllByPacienteAndDataHora(paciente, dataHora);
    // }
    // public List<Consulta> listarConsultasPorMedicoDataHoraIntervalo(User medico, LocalDateTime dataHoraInicial, LocalDateTime dataHoraFinal) {
    //     return consultaRepository.findAllByMedicoAndDataHoraBetween(medico, dataHoraInicial, dataHoraFinal);
    // }
    
}