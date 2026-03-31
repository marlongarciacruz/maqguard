package com.proyectogaes.entity;
 
import java.math.BigDecimal;
import java.time.LocalDate;
 

public class MantenimientoDTO {
 
    private Long idMantenimiento;
    private String descripcionTrabajo;
    private LocalDate fechaMantenimiento;
    private BigDecimal costoMantenimiento;
    private String estado;
    private String tipoMantenimiento;
 
    
    private String nombreMaquina;
    private String modeloMaquina;
    private String ubicacionMaquina;
    private String nombreTecnico;
 
  
    public MantenimientoDTO(Long idMantenimiento, String descripcionTrabajo,
                             LocalDate fechaMantenimiento, BigDecimal costoMantenimiento,
                             String estado, String nombreMaquina,
                             String modeloMaquina, String ubicacionMaquina) {
        this.idMantenimiento = idMantenimiento;
        this.descripcionTrabajo = descripcionTrabajo;
        this.fechaMantenimiento = fechaMantenimiento;
        this.costoMantenimiento = costoMantenimiento;
        this.estado = estado;
        this.nombreMaquina = nombreMaquina;
        this.modeloMaquina = modeloMaquina;
        this.ubicacionMaquina = ubicacionMaquina;
    }
 
    
    public MantenimientoDTO(Long idMantenimiento, LocalDate fechaMantenimiento,
                             String tipoMantenimiento, String estado,
                             String nombreMaquina, String nombreTecnico) {
        this.idMantenimiento = idMantenimiento;
        this.fechaMantenimiento = fechaMantenimiento;
        this.tipoMantenimiento = tipoMantenimiento;
        this.estado = estado;
        this.nombreMaquina = nombreMaquina;
        this.nombreTecnico = nombreTecnico;
    }
 
   
    public Long getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(Long idMantenimiento) { this.idMantenimiento = idMantenimiento; }
 
    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public void setDescripcionTrabajo(String descripcionTrabajo) { this.descripcionTrabajo = descripcionTrabajo; }
 
    public LocalDate getFechaMantenimiento() { return fechaMantenimiento; }
    public void setFechaMantenimiento(LocalDate fechaMantenimiento) { this.fechaMantenimiento = fechaMantenimiento; }
 
    public BigDecimal getCostoMantenimiento() { return costoMantenimiento; }
    public void setCostoMantenimiento(BigDecimal costoMantenimiento) { this.costoMantenimiento = costoMantenimiento; }
 
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
 
    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String tipoMantenimiento) { this.tipoMantenimiento = tipoMantenimiento; }
 
    public String getNombreMaquina() { return nombreMaquina; }
    public void setNombreMaquina(String nombreMaquina) { this.nombreMaquina = nombreMaquina; }
 
    public String getModeloMaquina() { return modeloMaquina; }
    public void setModeloMaquina(String modeloMaquina) { this.modeloMaquina = modeloMaquina; }
 
    public String getUbicacionMaquina() { return ubicacionMaquina; }
    public void setUbicacionMaquina(String ubicacionMaquina) { this.ubicacionMaquina = ubicacionMaquina; }
 
    public String getNombreTecnico() { return nombreTecnico; }
    public void setNombreTecnico(String nombreTecnico) { this.nombreTecnico = nombreTecnico; }
}