package com.natixis.java.gestao_consultas.repository;

import com.natixis.java.gestao_consultas.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}