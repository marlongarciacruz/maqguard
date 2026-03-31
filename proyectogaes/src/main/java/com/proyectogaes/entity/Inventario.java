package com.proyectogaes.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_repuesto;

    private String nombre;
    private String codigo_pieza;
    private Integer cantidad;
    private String ubicacion_almacen;
    private Integer stock_minimo;

    // getters y setters
    public Long getId_repuesto() {
        return id_repuesto;
    }

    public void setId_repuesto(Long id_repuesto) {
        this.id_repuesto = id_repuesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo_pieza() {
        return codigo_pieza;
    }

    public void setCodigo_pieza(String codigo_pieza) {
        this.codigo_pieza = codigo_pieza;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUbicacion_almacen() {
        return ubicacion_almacen;
    }

    public void setUbicacion_almacen(String ubicacion_almacen) {
        this.ubicacion_almacen = ubicacion_almacen;
    }

    public Integer getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(Integer stock_minimo) {
        this.stock_minimo = stock_minimo;
    }
}