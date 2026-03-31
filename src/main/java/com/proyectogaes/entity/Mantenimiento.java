package com.proyectogaes.entity;
 
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
 
@Entity
@Table(name = "mantenimientos")
public class Mantenimiento {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long idMantenimiento;
 
    @Column(name = "id_maquina")
    private Long idMaquina;
 
    @Column(name = "id_usuario")
    private Long idUsuario;
 
    @Column(name = "fecha_mantenimiento")
    private LocalDate fechaMantenimiento;
 
    @Column(name = "tipo_mantenimiento")
    private String tipoMantenimiento;
 
    @Column(name = "descripcion_trabajo", columnDefinition = "TEXT")
    private String descripcionTrabajo;
 
    @Column(name = "costo_mantenimiento")
    private BigDecimal costoMantenimiento;
 
    @Column(name = "estado")
    private String estado;
 
   
    @Transient
    private String nombreMaquina;
 
    @Transient
    private String modeloMaquina;
 
    @Transient
    private String ubicacionMaquina;
 
    @Transient
    private String nombreTecnico;
 
   
 
    public Long getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(Long idMantenimiento) { this.idMantenimiento = idMantenimiento; }
 
    public Long getIdMaquina() { return idMaquina; }
    public void setIdMaquina(Long idMaquina) { this.idMaquina = idMaquina; }
 
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
 
    public LocalDate getFechaMantenimiento() { return fechaMantenimiento; }
    public void setFechaMantenimiento(LocalDate fechaMantenimiento) { this.fechaMantenimiento = fechaMantenimiento; }
 
    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String tipoMantenimiento) { this.tipoMantenimiento = tipoMantenimiento; }
 
    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public void setDescripcionTrabajo(String descripcionTrabajo) { this.descripcionTrabajo = descripcionTrabajo; }
 
    public BigDecimal getCostoMantenimiento() { return costoMantenimiento; }
    public void setCostoMantenimiento(BigDecimal costoMantenimiento) { this.costoMantenimiento = costoMantenimiento; }
 
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
 
    public String getNombreMaquina() { return nombreMaquina; }
    public void setNombreMaquina(String nombreMaquina) { this.nombreMaquina = nombreMaquina; }
 
    public String getModeloMaquina() { return modeloMaquina; }
    public void setModeloMaquina(String modeloMaquina) { this.modeloMaquina = modeloMaquina; }
 
    public String getUbicacionMaquina() { return ubicacionMaquina; }
    public void setUbicacionMaquina(String ubicacionMaquina) { this.ubicacionMaquina = ubicacionMaquina; }
 
    public String getNombreTecnico() { return nombreTecnico; }
    public void setNombreTecnico(String nombreTecnico) { this.nombreTecnico = nombreTecnico; }
}