package com.natixis.java.gestao_consultas.repository;

import com.natixis.java.gestao_consultas.model.Consulta;
import com.natixis.java.gestao_consultas.model.EstadoConsulta;
import com.natixis.java.gestao_consultas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Consultas por paciente
    List<Consulta> findByPaciente(User paciente);
    
    // Consultas por médico
    List<Consulta> findByMedico(User medico);
    
    // Consultas por médico e estado
    List<Consulta> findByMedicoAndEstado(User medico, EstadoConsulta estado);
    
    // Consultas por paciente e estado
    List<Consulta> findByPacienteAndEstado(User paciente, EstadoConsulta estado);
    
    // Verifica se existe consulta conflitante para um médico
    boolean existsByMedicoAndDataHoraBetween(User medico, LocalDateTime inicio, LocalDateTime fim);
    
    // Consultas por médico em um período específico
    @Query("SELECT c FROM Consulta c WHERE c.medico = :medico AND c.dataHora BETWEEN :inicio AND :fim")
    List<Consulta> findConsultasPorMedicoEPeriodo(
        @Param("medico") User medico,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim);
    
    // Consultas por paciente em um período específico
    @Query("SELECT c FROM Consulta c WHERE c.paciente = :paciente AND c.dataHora BETWEEN :inicio AND :fim")
    List<Consulta> findConsultasPorPacienteEPeriodo(
        @Param("paciente") User paciente,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim);
    
    // Contagem de consultas por estado (para dashboard)
    @Query("SELECT c.estado, COUNT(c) FROM Consulta c GROUP BY c.estado")
    List<Object[]> countConsultasByEstado();
    
    // Consultas pendentes próximas (para notificações)
    @Query("SELECT c FROM Consulta c WHERE c.estado = 'PENDENTE' AND c.dataHora BETWEEN :agora AND :limite")
    List<Consulta> findConsultasPendentesProximas(
        @Param("agora") LocalDateTime agora,
        @Param("limite") LocalDateTime limite);
    
    // Consultas que precisam ser automaticamente concluídas (após horário)
    @Query("SELECT c FROM Consulta c WHERE c.estado = 'PENDENTE' AND c.dataHora < :agora")
    List<Consulta> findConsultasParaConcluirAutomaticamente(
        @Param("agora") LocalDateTime agora);
}