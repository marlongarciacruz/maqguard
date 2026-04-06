package com.proyectogaes.controller;

import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class InicioController {

    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio"; 
    }

    @GetMapping("/login")
    public String mostrarPaginaLogin() {
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio(Authentication authentication, HttpServletResponse response, Model model) {
        // 1. Configuración de cabeceras para evitar el botón "Atrás" después del logout
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        if (authentication == null) {
            return "redirect:/login";
        }

        // 2. Identificación de Roles
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        
        boolean isTecnico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));

        // 3. Lógica para el ADMINISTRADOR
        if (isAdmin) {
            // Variables que requiere el diseño de dasboardadmin.html
            model.addAttribute("totalMaquinas", 0);
            model.addAttribute("mantenimientosActivos", 0);
            model.addAttribute("repuestosBajos", 0);
            model.addAttribute("alertasFallas", 0);
            model.addAttribute("totalNotificaciones", 0);
            model.addAttribute("ultimasNotificaciones", new ArrayList<>()); // Lista vacía para evitar error en th:each
            
            return "dasboardadmin"; 
        } 
        
        // 4. Lógica para el TÉCNICO
        if (isTecnico) {
            // Variables que requiere el diseño de dashboardtecnico.html
            model.addAttribute("nombreTecnico", authentication.getName());
            model.addAttribute("mantenimientosAsignados", 0); 
            model.addAttribute("reportesFallas", 0);
            model.addAttribute("notificaciones", 0);
            model.addAttribute("totalNoLeidas", 0); 
            model.addAttribute("notificacionesNav", new ArrayList<>()); // Lista vacía para evitar error en th:each
            
            return "dashboardtecnico";
        }

        return "redirect:/login";
    }
}