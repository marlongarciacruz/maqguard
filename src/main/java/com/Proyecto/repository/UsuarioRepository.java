package com.Proyecto.repository;

import com.Proyecto.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE Usuario u SET u.activo = false WHERE u.id = :id")
    void desactivarById(@Param("id") Long id);
}