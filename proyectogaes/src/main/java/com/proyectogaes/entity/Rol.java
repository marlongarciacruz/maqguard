package com.proyectogaes.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer id;

    // Forzamos el nombre exacto para que Lombok genere getDescripcion_rol()
    @Column(name = "descripcion_rol")
    private String descripcion_rol; 
}