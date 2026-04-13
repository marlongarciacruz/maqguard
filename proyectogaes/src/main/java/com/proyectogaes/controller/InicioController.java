package com.proyectogaes.controller;

import com.proyectogaes.entity.Usuario;
import com.proyectogaes.repository.MantenimientoRepository;
import com.proyectogaes.repository.ReporteFallaRepository;
import com.proyectogaes.repository.MaquinasRepository;
import com.proyectogaes.repository.UsuarioRepository;
import com.proyectogaes.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Controller
public class InicioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MaquinasRepository maquinasRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private MantenimientoRepository mantenimientosRepository;

    @Autowired
    private ReporteFallaRepository reporteFallasRepository;

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

        // 1. Seguridad: Limpiar caché para evitar acceso post-logout
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Obtener datos del usuario en sesión
        Usuario usuarioSesion = usuarioRepository.findByUsername(authentication.getName()).orElse(null);
        model.addAttribute("usuarioSesion", usuarioSesion);

        // 3. Identificación de Roles
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        boolean isTecnico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));

        // 4. Lógica para el ADMINISTRADOR
        if (isAdmin) {
            // Datos reales para las KPI Cards
            model.addAttribute("totalMaquinas", maquinasRepository.count());
            model.addAttribute("mantenimientosActivos", mantenimientosRepository.count());

            // Usando el método personalizado para stock crítico
            model.addAttribute("repuestosBajos", inventarioRepository.countByCantidadLessThanEqual(5));

            model.addAttribute("alertasFallas", reporteFallasRepository.count());

            // Datos complementarios
            model.addAttribute("totalNotificaciones", 0);
            model.addAttribute("ultimasNotificaciones", new ArrayList<>());

            // Retorna la vista: dasboardadmin.html
            return "dasboardadmin";
        }

        // 5. Lógica para el TÉCNICO
        if (isTecnico) {
            model.addAttribute("nombreTecnico",
                    usuarioSesion != null ? usuarioSesion.getNombre() : authentication.getName());

            model.addAttribute("mantenimientosAsignados", 0);
            model.addAttribute("reportesFallas", 0);
            model.addAttribute("notificaciones", 0);
            model.addAttribute("totalNoLeidas", 0);
            model.addAttribute("notificacionesNav", new ArrayList<>());

            return "dashboardtecnico";
        }

        return "redirect:/login";
    }

    @PostMapping("/usuarios/cambiar-contrasena-perfil")
    public String cambiarPropiaContrasena(@RequestParam String nueva_contrasena, Authentication authentication,
            RedirectAttributes ra) {
        try {
            // 1. Buscamos al usuario que está logueado
            Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElse(null);

            if (usuario != null) {
                // 2. Encriptamos la nueva clave (Asumiendo que usas BCrypt)
                // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                // usuario.setPassword(encoder.encode(nueva_contrasena));

                // Si por ahora no usas encriptación (no recomendado):
                usuario.setPassword(nueva_contrasena);

                usuarioRepository.save(usuario);
                ra.addFlashAttribute("success", "¡Contraseña actualizada correctamente!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar la contraseña.");
        }
        return "redirect:/inicio";
    }
}