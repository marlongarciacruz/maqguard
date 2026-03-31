package com.proyectogaes.repository;

import com.proyectogaes.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    
    @Query("SELECT m FROM Mantenimiento m " +
           "JOIN FETCH m.maquina " +
           "JOIN FETCH m.usuario")
    List<Mantenimiento> findAllConRelaciones();

    @Query("SELECT m FROM Mantenimiento  m WHERE m.estado = :estado ORDER BY m.fechaMantenimiento DESC")
    List<Mantenimiento> buscarPorEstado(@Param("estado") String estado);

    @Query("SELECT m FROM Mantenimiento m WHERE m.usuario.id = :idTecnico AND m.estado = :estado ORDER BY m.fechaMantenimiento DESC")
    List<Mantenimiento> buscarPorTecnicoYEstado(@Param("idTecnico") Integer idTecnico, @Param("estado") String estado);

    @Query("SELECT m FROM Mantenimiento m WHERE m.usuario.id = :idUsuario")
    List<Mantenimiento> buscarPorUsuario(@Param("idUsuario") Integer idUsuario);
}