package com.proyectogaes.entity;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
 

public class MantenimientoDetalleDTO {
 
    
    private Long idMantenimiento;
    private Long idMaquina;
    private LocalDate fechaMantenimiento;
    private String tipoMantenimiento;
    private String descripcionTrabajo;
    private BigDecimal costoMantenimiento;
    private String estado;
 
  
    private String nombreMaquina;
 
   
    private String nombreTecnico;
 
   
    private String detallesCompletos;
 
   
    private LocalDateTime fechaInicio;
 
 
    private LocalDateTime fechaFin;
 
   
    public MantenimientoDetalleDTO(Long idMantenimiento, Long idMaquina,
                                    LocalDate fechaMantenimiento, String tipoMantenimiento,
                                    String descripcionTrabajo, BigDecimal costoMantenimiento,
                                    String estado, String nombreMaquina, String nombreTecnico,
                                    String detallesCompletos, LocalDateTime fechaInicio,
                                    LocalDateTime fechaFin) {
        this.idMantenimiento = idMantenimiento;
        this.idMaquina = idMaquina;
        this.fechaMantenimiento = fechaMantenimiento;
        this.tipoMantenimiento = tipoMantenimiento;
        this.descripcionTrabajo = descripcionTrabajo;
        this.costoMantenimiento = costoMantenimiento;
        this.estado = estado;
        this.nombreMaquina = nombreMaquina;
        this.nombreTecnico = nombreTecnico;
        this.detallesCompletos = detallesCompletos;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
 
 
    public Long getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(Long v) { this.idMantenimiento = v; }
 
    public Long getIdMaquina() { return idMaquina; }
    public void setIdMaquina(Long v) { this.idMaquina = v; }
 
    public LocalDate getFechaMantenimiento() { return fechaMantenimiento; }
    public void setFechaMantenimiento(LocalDate v) { this.fechaMantenimiento = v; }
 
    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String v) { this.tipoMantenimiento = v; }
 
    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public void setDescripcionTrabajo(String v) { this.descripcionTrabajo = v; }
 
    public BigDecimal getCostoMantenimiento() { return costoMantenimiento; }
    public void setCostoMantenimiento(BigDecimal v) { this.costoMantenimiento = v; }
 
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
 
    public String getNombreMaquina() { return nombreMaquina; }
    public void setNombreMaquina(String v) { this.nombreMaquina = v; }
 
    public String getNombreTecnico() { return nombreTecnico; }
    public void setNombreTecnico(String v) { this.nombreTecnico = v; }
 
    public String getDetallesCompletos() { return detallesCompletos; }
    public void setDetallesCompletos(String v) { this.detallesCompletos = v; }
 
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime v) { this.fechaInicio = v; }
 
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime v) { this.fechaFin = v; }
}
 