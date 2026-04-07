package com.proyectogaes.controller;

import java.util.ArrayList;

// Importo Spring Security para saber quién está logueado y qué permisos tiene
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class InicioController {

    // La ruta raíz: si alguien entra al dominio pelado, lo mando a la página de
    // inicio
    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio";
    }

    // Ruta para mostrar mi formulario de login personalizado
    @GetMapping("/login")
    public String mostrarPaginaLogin() {
        return "login";
    }

    // Esta es la ruta principal después de loguearse, aquí decido a dónde mandarlos
    @GetMapping("/inicio")
    public String inicio(Authentication authentication, HttpServletResponse response, Model model) {

        // 1. Truco de seguridad: limpio el caché para que si cierran sesión, no puedan
        // volver atrás con el navegador
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // Si por alguna razón la autenticación es nula, patitas para la calle (al
        // login)
        if (authentication == null) {
            return "redirect:/login";
        }

        // 2. Reviso qué rol tiene el que entró: ¿Es administrador o es técnico?
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        boolean isTecnico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));

        // 3. Si es ADMIN, preparo los datos de su dashboard (contadores de máquinas,
        // alertas, etc.)
        if (isAdmin) {
            // Dejo estos en 0 o vacíos por ahora para que el HTML no explote al cargar los
            // fragmentos
            model.addAttribute("totalMaquinas", 0);
            model.addAttribute("mantenimientosActivos", 0);
            model.addAttribute("repuestosBajos", 0);
            model.addAttribute("alertasFallas", 0);
            model.addAttribute("totalNotificaciones", 0);
            model.addAttribute("ultimasNotificaciones", new ArrayList<>()); // Lista vacía para que el th:each no de
                                                                            // error

            return "dasboardadmin";
        }

        // 4. Si es TÉCNICO, le muestro sus mantenimientos asignados y su nombre de
        // usuario
        if (isTecnico) {
            model.addAttribute("nombreTecnico", authentication.getName());
            model.addAttribute("mantenimientosAsignados", 0);
            model.addAttribute("reportesFallas", 0);
            model.addAttribute("notificaciones", 0);
            model.addAttribute("totalNoLeidas", 0);
            model.addAttribute("notificacionesNav", new ArrayList<>()); // Evito errores en el bucle del menú

            return "dashboardtecnico";
        }

        // Si entró alguien con un rol que no reconozco, mejor lo mando al login de
        // nuevo
        return "redirect:/login";
    }
}