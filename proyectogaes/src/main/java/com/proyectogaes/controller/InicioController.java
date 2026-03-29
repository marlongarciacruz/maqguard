package com.proyectogaes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class InicioController {
    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio"; // Retorna el nombre de la vista (inicio.html)
    }

    @GetMapping("/login")
    public String mostrarPaginaLogin() {
        return "login"; // Retorna el nombre de la vista (login.html)
    }

    @GetMapping("/inicio")
    public String inicio(HttpServletResponse response) {

        // 🔥 EVITA QUE EL NAVEGADOR GUARDE CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return "dasboardadmin";
    }
}