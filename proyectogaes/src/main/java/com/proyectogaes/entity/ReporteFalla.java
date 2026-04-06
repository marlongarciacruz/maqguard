package com.proyectogaes.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reporte_falla")
public class ReporteFalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_falla") // Coincide con la imagen de tu DB
    private Long idFalla;

    private String titulo;

    @Column(name = "descripcion_falla", columnDefinition = "TEXT")
    private String descripcionFalla;

    @Column(name = "id_usuario")
    private Long idUsuario;

    private String prioridad;

    private String estado;

    @Column(name = "fecha_reporte")
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_maquina")
    private Long idMaquina;

    // --- MÉTODOS AUTOMÁTICOS PARA FECHAS ---

    @PrePersist
    protected void onCreate() {
        this.fechaReporte = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "pendiente"; // Estado por defecto según tu captura
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS ---

    public Long getIdFalla() { return idFalla; }
    public void setIdFalla(Long idFalla) { this.idFalla = idFalla; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcionFalla() { return descripcionFalla; }
    public void setDescripcionFalla(String descripcionFalla) { this.descripcionFalla = descripcionFalla; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDateTime fechaReporte) { this.fechaReporte = fechaReporte; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Long getIdMaquina() { return idMaquina; }
    public void setIdMaquina(Long idMaquina) { this.idMaquina = idMaquina; }
}