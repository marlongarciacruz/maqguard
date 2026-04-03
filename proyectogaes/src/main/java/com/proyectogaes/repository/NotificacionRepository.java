package com.proyectogaes.repository;

import com.proyectogaes.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :idUsuario ORDER BY n.fechaCreacion DESC")
    List<Notificacion> listarPorUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT n FROM Notificacion n ORDER BY n.fechaCreacion DESC")
    List<Notificacion> listarTodas();

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leida = 1 WHERE n.usuario.id = :idUsuario AND n.leida = 0")
    void marcarTodasComoLeidas(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leida = 1 WHERE n.leida = 0")
    void marcarTodasComoLeidasAdmin();
}