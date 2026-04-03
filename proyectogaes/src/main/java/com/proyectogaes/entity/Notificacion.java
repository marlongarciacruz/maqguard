package com.proyectogaes.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data // Requiere Lombok para ahorrar getters/setters
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Relación con la tabla usuarios

    private String titulo;
    private String mensaje;
    private String tipo;
    
    @Column(name = "leida")
    private Integer leida = 0; // 0: Pendiente, 1: Leída

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Método de conveniencia para la lógica de la vista
    public String getEstado() {
        return (leida == 1) ? "Leída" : "Pendiente";
    }
}