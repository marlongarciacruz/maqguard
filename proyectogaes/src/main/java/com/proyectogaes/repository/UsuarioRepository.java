package com.proyectogaes.repository;

import com.proyectogaes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Funciona directo porque el campo en la entidad se llama 'username'
    Optional<Usuario> findByUsername(String username);

    // Buscar por estado (Activo / Inactivo)
    List<Usuario> findByEstado(String estado);

    // Buscar por rol
    List<Usuario> findByRolId(int idRol);
}