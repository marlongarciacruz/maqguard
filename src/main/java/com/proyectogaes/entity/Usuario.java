package com.proyectogaes.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    private String nombre;

    @Column(name = "usuario")
    private String username;

    @Column(name = "contrasena")
    private String password;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
}