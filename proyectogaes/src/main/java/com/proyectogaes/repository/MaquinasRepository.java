package com.proyectogaes.repository;

import com.proyectogaes.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface MaquinasRepository extends JpaRepository<Maquina, Long> {
    
    @Query("SELECT m FROM Maquina m WHERE m.fechaProximoMantenimiento BETWEEN :hoy AND :limite")
    List<Maquina> buscarAlertas(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);

    // Estos métodos evitan que Spring intente autogenerar la query y falle por el guion bajo
    @Query("SELECT COUNT(m) > 0 FROM Maquina m WHERE m.nombre = :nombre")
    boolean nombreExiste(@Param("nombre") String nombre);

    @Query("SELECT COUNT(m) > 0 FROM Maquina m WHERE m.modelo = :modelo")
    boolean modeloExiste(@Param("modelo") String modelo);
    
    @Query("SELECT COUNT(m) > 0 FROM Maquina m WHERE m.nombre = :nombre AND m.id_maquina != :id")
    boolean nombreExisteEnOtro(@Param("nombre") String nombre, @Param("id") Long id);
    
    @Query("SELECT COUNT(m) > 0 FROM Maquina m WHERE m.modelo = :modelo AND m.id_maquina != :id")
    boolean modeloExisteEnOtro(@Param("modelo") String modelo, @Param("id") Long id);
}