package com.natixis.java.gestao_consultas.repository;

import com.natixis.java.gestao_consultas.model.Consulta;
import com.natixis.java.gestao_consultas.model.Consulta.EstadoConsulta;
import com.natixis.java.gestao_consultas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);

    List<Consulta> findByPacienteAndDataHoraBetween(User paciente, LocalDateTime start, LocalDateTime end);

    // Consultar por data
    List<Consulta> findByDataHora(LocalDateTime dataHora);

    // Consultas por paciente
    List<Consulta> findByPaciente(User paciente);
    
    // Consultas por médico
    List<Consulta> findByMedico(User medico);

    // // Consultas por médico e estado
    // List<Consulta> findByMedicoAndEstado(User medico, EstadoConsulta estado);
    
    // // Consultas por paciente e estado
    // List<Consulta> findByPacienteAndEstado(User paciente, EstadoConsulta estado);
    
    // Verifica se existe consulta conflitante para um médico
    boolean existsByMedicoAndDataHoraBetween(User medico, LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByDataHoraBetweenAndEstado(LocalDateTime inicio, LocalDateTime fim, EstadoConsulta estado);

    List<Consulta> findByEstado(EstadoConsulta estado);

    List<Consulta> findByPacienteAndEstado(User paciente, EstadoConsulta estado);

    List<Consulta> findByPacienteAndDataHoraBetweenAndEstado(User paciente, LocalDateTime inicio, LocalDateTime fim,
            EstadoConsulta estado);

}