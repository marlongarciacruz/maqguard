package com.proyectogaes.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "maquinas")
@Data
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_maquina;

    private String nombre;
    private String modelo;
    private String ubicacion;

    @Column(name = "fecha_ultimo_mantenimiento")
    private LocalDate fechaUltimoMantenimiento;

    @Column(name = "intervalo_mantenimiento")
    private Integer intervaloMantenimiento;

    @Column(name = "fecha_proximo_mantenimiento")
    private LocalDate fechaProximoMantenimiento;

    private String estado;

    // --- REPLICANDO CALCULAR PROXIMO MANTENIMIENTO ---
    @PrePersist
    @PreUpdate
    public void calcularProximoMantenimiento() {
        if (this.fechaUltimoMantenimiento != null && this.intervaloMantenimiento != null) {
            // Suma los días del intervalo a la última fecha
            this.fechaProximoMantenimiento = this.fechaUltimoMantenimiento.plusDays(this.intervaloMantenimiento);
        }
    }
}