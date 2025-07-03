package com.natixis.java.gestao_consultas.repository;

import com.natixis.java.gestao_consultas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRoles_Name(String roleName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}