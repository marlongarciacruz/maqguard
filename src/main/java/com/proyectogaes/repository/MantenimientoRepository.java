package com.proyectogaes.repository;
 
import com.proyectogaes.entity.Mantenimiento;
import com.proyectogaes.entity.MantenimientoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
 

    @Query("""
        SELECT new com.proyectogaes.entity.MantenimientoDTO(
            m.idMantenimiento,
            m.descripcionTrabajo,
            m.fechaMantenimiento,
            m.costoMantenimiento,
            m.estado,
            maq.nombre,
            maq.modelo,
            maq.ubicacion
        )
        FROM Mantenimiento m
        JOIN Maquina maq ON maq.idMaquina = m.idMaquina
        WHERE m.idUsuario = :idUsuario
          AND m.estado <> 'Finalizado'
        ORDER BY m.fechaMantenimiento DESC
        """)
    List<MantenimientoDTO> findAsignadosByTecnico(@Param("idUsuario") Long idUsuario);
 
   
    @Query("""
        SELECT new com.proyectogaes.entity.MantenimientoDTO(
            m.idMantenimiento,
            m.fechaMantenimiento,
            m.tipoMantenimiento,
            m.estado,
            maq.nombre,
            u.nombre
        )
        FROM Mantenimiento m
        JOIN Maquina maq ON maq.idMaquina = m.idMaquina
        JOIN Usuario u   ON u.idUsuario   = m.idUsuario
        WHERE m.idUsuario = :idUsuario
          AND m.estado    = :estado
        ORDER BY m.fechaMantenimiento DESC
        """)
    List<MantenimientoDTO> findHistorialByTecnico(@Param("idUsuario") Long idUsuario,
                                                   @Param("estado") String estado);
 
   
    @Query("""
        SELECT new com.proyectogaes.entity.MantenimientoDTO(
            m.idMantenimiento,
            m.descripcionTrabajo,
            m.fechaMantenimiento,
            m.costoMantenimiento,
            m.estado,
            maq.nombre,
            maq.modelo,
            maq.ubicacion
        )
        FROM Mantenimiento m
        JOIN Maquina maq ON maq.idMaquina = m.idMaquina
        WHERE m.idMantenimiento = :id
        """)
    Optional<MantenimientoDTO> findDetalleConMaquina(@Param("id") Long id);
 
    
    @Query("""
        SELECT new com.proyectogaes.entity.MantenimientoDetalleDTO(
            m.idMantenimiento,
            m.idMaquina,
            m.fechaMantenimiento,
            m.tipoMantenimiento,
            m.descripcionTrabajo,
            m.costoMantenimiento,
            m.estado,
            maq.nombre,
            u.nombre,
            dm.descripcionTrabajo,
            dm.fechaInicio,
            dm.fechaFin
        )
        FROM Mantenimiento m
        JOIN Maquina  maq ON maq.idMaquina = m.idMaquina
        JOIN Usuario  u   ON u.idUsuario   = m.idUsuario
        LEFT JOIN DetalleMantenimiento dm ON dm.idMantenimiento = m.idMantenimiento
        WHERE m.idMantenimiento = :id
        """)
    Optional<MantenimientoDetalleDTO> findDetalleCompleto(@Param("id") Long id);
 
  
    long countByIdUsuario(Long idUsuario);
 
    long countByIdUsuarioAndEstadoNot(Long idUsuario, String estado);
}
 