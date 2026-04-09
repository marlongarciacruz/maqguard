package com.proyectogaes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "maquinas")
@Data
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_maquina;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 30, message = "El nombre no puede exceder los 30 caracteres")
    @Column(length = 30)
    private String nombre;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 20, message = "El modelo no puede exceder los 20 caracteres")
    @Column(length = 20)
    private String modelo;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 50, message = "La ubicación no puede exceder los 50 caracteres")
    @Column(length = 50)
    private String ubicacion;

    @NotNull(message = "La fecha de último mantenimiento es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    @Column(name = "fecha_ultimo_mantenimiento")
    private LocalDate fechaUltimoMantenimiento;

    @NotNull(message = "El intervalo es obligatorio")
    @Min(value = 1, message = "Mínimo 1 día")
    @Max(value = 365, message = "Máximo 365 días")
    @Column(name = "intervalo_mantenimiento")
    private Integer intervaloMantenimiento;

    @Column(name = "fecha_proximo_mantenimiento")
    private LocalDate fechaProximoMantenimiento;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @PrePersist
    @PreUpdate
    public void calcularProximoMantenimiento() {
        if (this.fechaUltimoMantenimiento != null && this.intervaloMantenimiento != null) {
            this.fechaProximoMantenimiento = this.fechaUltimoMantenimiento.plusDays(this.intervaloMantenimiento);
        }
    }
}