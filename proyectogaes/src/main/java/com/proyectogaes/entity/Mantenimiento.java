package com.proyectogaes.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mantenimientos")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_mantenimiento;

    @ManyToOne
    @JoinColumn(name = "id_maquina")
    private Maquina maquina;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_mantenimiento")
    private LocalDate fechaMantenimiento;

    @Column(name = "tipo_mantenimiento")
    private String tipoMantenimiento;

    @Column(name = "descripcion_trabajo", columnDefinition = "TEXT")
    private String descripcionTrabajo;

    @Column(name = "costo_mantenimiento")
    private Double costoMantenimiento;

    private String estado;

    // ✅ GETTERS Y SETTERS

    public Long getId_mantenimiento() {
        return id_mantenimiento;
    }

    public void setId_mantenimiento(Long id_mantenimiento) {
        this.id_mantenimiento = id_mantenimiento;
    }

    public Maquina getMaquina() {
        return maquina;
    }

    public void setMaquina(Maquina maquina) {
        this.maquina = maquina;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFechaMantenimiento() {
        return fechaMantenimiento;
    }

    public void setFechaMantenimiento(LocalDate fechaMantenimiento) {
        this.fechaMantenimiento = fechaMantenimiento;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public String getDescripcionTrabajo() {
        return descripcionTrabajo;
    }

    public void setDescripcionTrabajo(String descripcionTrabajo) {
        this.descripcionTrabajo = descripcionTrabajo;
    }

    public Double getCostoMantenimiento() {
        return costoMantenimiento;
    }

    public void setCostoMantenimiento(Double costoMantenimiento) {
        this.costoMantenimiento = costoMantenimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}