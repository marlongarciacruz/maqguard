package com.proyectogaes.model;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reporte_falla")
public class ReporteFalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFalla;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcionFalla;

    private Long idUsuario;

    private String prioridad;

    private String estado;

    private LocalDateTime fechaReporte;

    private LocalDateTime fechaActualizacion;

    private Long idMaquina;

    // Getters y Setters
}