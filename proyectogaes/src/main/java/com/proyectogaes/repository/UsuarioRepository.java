package com.proyectogaes.repository;

import com.proyectogaes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Este es el método que Spring sí entiende
    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByEstado(String estado);

    // Ajustado para que coincida con la relación en la entidad

    List<Usuario> findByRolId(int idRol);
    // BORRA cualquier método que se llame findByUsuario(...)
}