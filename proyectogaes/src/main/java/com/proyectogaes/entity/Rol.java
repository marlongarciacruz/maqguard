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

    // Cambiamos el nombre de la variable a camelCase
    @Column(name = "descripcion_rol")
    private String descripcionRol; 
}