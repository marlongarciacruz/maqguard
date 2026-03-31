package com.proyectogaes.repository;

import com.proyectogaes.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaquinasRepository extends JpaRepository<Maquina, Long> {

    // --- REPLICANDO GET ALERTAS PROXIMO MANTENIMIENTO ---
    @Query("SELECT m FROM Maquina m WHERE m.fechaProximoMantenimiento >= :hoy " +
           "AND m.fechaProximoMantenimiento <= :limite " +
           "AND m.estado = 'Activo' " +
           "ORDER BY m.fechaProximoMantenimiento ASC")
    List<Maquina> buscarAlertas(
        @Param("hoy") LocalDate hoy, 
        @Param("limite") LocalDate limite
    );
}