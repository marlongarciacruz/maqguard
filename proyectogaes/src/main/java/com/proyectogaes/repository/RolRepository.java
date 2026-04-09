package com.proyectogaes.repository;

import com.proyectogaes.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Buscar rol por su descripción (ej: "ADMIN", "TECNICO")
    Rol findByDescripcionRol(String descripcionRol);
}