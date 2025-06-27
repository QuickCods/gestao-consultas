package com.natixis.java.gestao_consultas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
public class Consulta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private User paciente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private User medico;
    
    @Future
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConsulta estado = EstadoConsulta.PENDENTE;
    
    @NotBlank
    @Size(max = 500)
    private String descricao;
    
    // Getters, Setters, Construtores

    public enum EstadoConsulta {
        PENDENTE,
        CONCLUIDA,
        CANCELADA
    }
    public Consulta() {}
    
    public Consulta(User paciente, User medico, LocalDateTime dataHora, EstadoConsulta estado, String descricao) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataHora = dataHora;
        this.estado = estado;
        this.descricao = descricao;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getPaciente() {
        return paciente;
    }
    public void setPaciente(User paciente) {
        this.paciente = paciente;
    }
    public User getMedico() {
        return medico;
    }
    public void setMedico(User medico) {
        this.medico = medico;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    public EstadoConsulta getEstado() {
        return estado;
    }
    public void setEstado(EstadoConsulta estado) {
        this.estado = estado;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public boolean isConcluida() {
        return this.estado == EstadoConsulta.CONCLUIDA;
    }
    public boolean isCancelada() {
        return this.estado == EstadoConsulta.CANCELADA;
    }
    public boolean isPendente() {
        return this.estado == EstadoConsulta.PENDENTE;
    }
    public boolean isAgendada() {
        return this.dataHora.isAfter(LocalDateTime.now());
    }
    
    
}