package com.proyectogaes.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 🔥 Cambia aquí la contraseña que quieras encriptar
        String passwordPlano = "123456";

        String passwordEncriptado = encoder.encode(passwordPlano);

        System.out.println("Contraseña encriptada:");
        System.out.println(passwordEncriptado);
    }
}