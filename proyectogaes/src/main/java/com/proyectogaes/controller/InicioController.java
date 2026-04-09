package com.proyectogaes.controller;

import com.proyectogaes.entity.Usuario;
import com.proyectogaes.repository.MaquinasRepository;
import com.proyectogaes.repository.UsuarioRepository;
import com.proyectogaes.repository.InventarioRepository; // Asegúrate de que este repo exista
// Importa otros repositorios si tienes tablas de Notificaciones o Mantenimientos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Controller
public class InicioController {

    @Autowired
    private MaquinasRepository maquinasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

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

        // 1. Seguridad: Limpiar caché
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Cargar datos del usuario logueado para el MODAL DE PERF
        // REEMPLAZA LA LÍNEA 52 Y 53 POR ESTO:
        Usuario usuarioSesion = usuarioRepository.findByUsername(authentication.getName()).orElse(null);
        model.addAttribute("usuarioSesion", usuarioSesion);
        // 3. Identificación de Roles
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        boolean isTecnico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));

        // 4. Lógica para el ADMINISTRADOR (Dashboard Real)
        if (isAdmin) {
            // Traemos conteos reales de la BD
            model.addAttribute("totalMaquinas", maquinasRepository.count());
            model.addAttribute("totalRepuestos", inventarioRepository.count());

            // Ejemplo de conteos con lógica (si tienes los métodos en el repo)
            // model.addAttribute("mantenimientosActivos",
            // mantenimientosRepository.countByEstado("PENDIENTE"));
            // model.addAttribute("alertasFallas",
            // reporteFallasRepository.countByEstado("ABIERTO"));

            // Valores por defecto para evitar errores en fragmentos de notificaciones
            model.addAttribute("totalNotificaciones", 0);
            model.addAttribute("ultimasNotificaciones", new ArrayList<>());

            return "dasboardadmin";
        }

        // 5. Lógica para el TÉCNICO
        if (isTecnico) {
            model.addAttribute("nombreTecnico",
                    usuarioSesion != null ? usuarioSesion.getNombre() : authentication.getName());

            // Conteos reales para el técnico
            // model.addAttribute("mantenimientosAsignados",
            // mantenimientosRepository.countByTecnico(usuarioSesion));

            model.addAttribute("reportesFallas", 0);
            model.addAttribute("notificaciones", 0);
            model.addAttribute("totalNoLeidas", 0);
            model.addAttribute("notificacionesNav", new ArrayList<>());

            return "dashboardtecnico";
        }

        return "redirect:/login";
    }
}