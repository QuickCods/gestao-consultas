package com.natixis.java.gestao_consultas.model;

import jakarta.persistence.*;

@Entity
// @Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name; // ROLE_PACIENTE, ROLE_MEDICO

    public Role(){}

    public Role(String name) {
        this.name = name;
    }
    
    // Getters e Setters

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}