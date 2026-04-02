package com.proyectogaes.controller;

import com.proyectogaes.entity.Inventario;
import com.proyectogaes.entity.Mantenimiento;
import com.proyectogaes.repository.UsuarioRepository;
import com.proyectogaes.service.MantenimientoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class TecnicoController {

    private final MantenimientoService mantenimientoService;
    private final UsuarioRepository usuarioRepository;

    public TecnicoController(MantenimientoService mantenimientoService,
                             UsuarioRepository usuarioRepository) {
        this.mantenimientoService = mantenimientoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Método auxiliar para obtener el ID del usuario logueado
    private Integer getIdUsuario(Authentication authentication) {
        return usuarioRepository.findByUsername(authentication.getName())
                .map(u -> u.getId())
                .orElse(null);
    }

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping("/tecnico/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        Integer idUsuario = getIdUsuario(authentication);
        model.addAttribute("nombreTecnico", authentication.getName());
        model.addAttribute("mantenimientosAsignados", mantenimientoService.countAsignados(idUsuario.longValue()));
        model.addAttribute("mantenimientosPendientes", mantenimientoService.countPendientes(idUsuario.longValue()));

        return "dashboardtecnico";
    }

    // ── Mis Mantenimientos ─────────────────────────────────────
    @GetMapping("/tecnico/mismantenimientos")
    public String misMantenimientos(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        Integer idUsuario = getIdUsuario(authentication);
        List<Mantenimiento> mantenimientos = mantenimientoService.getMisMantenimientos(idUsuario.longValue());
        model.addAttribute("mantenimientos", mantenimientos);
        model.addAttribute("titulo", "Mis Tareas de Mantenimiento");

        return "tecnico/mismantenimientos";
    }

    // ── Historial ──────────────────────────────────────────────
    @GetMapping("/tecnico/historial")
    public String historial(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        Integer idUsuario = getIdUsuario(authentication);
        List<Mantenimiento> historial = mantenimientoService.getHistorialTecnico(idUsuario.longValue());
        model.addAttribute("historial", historial);

        return "tecnico/historialtec";
    }

    // ── Iniciar mantenimiento ──────────────────────────────────
    @GetMapping("/tecnico/mismantenimientos/{id}/iniciar")
    public String iniciar(@PathVariable Long id,
                          Authentication authentication,
                          Model model) {
        if (authentication == null) return "redirect:/login";

        Mantenimiento mantenimiento = mantenimientoService.getDetalle(id);
        List<Inventario> repuestos = mantenimientoService.getRepuestosDisponibles();

        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("repuestos", repuestos);

        return "tecnico/iniciar";
    }

    // ── Finalizar mantenimiento ────────────────────────────────
    @PostMapping("/tecnico/mismantenimientos/{id}/finalizar")
    public String finalizar(@PathVariable Long id,
                            @RequestParam("descripcion_trabajo") String descripcion,
                            @RequestParam("fecha_ultimo_mantenimiento") String fechaUltimo,
                            @RequestParam("intervalo_mantenimiento") Integer intervalo,
                            @RequestParam(value = "repuestos", required = false) Map<Long, Integer> repuestos,
                            RedirectAttributes redirectAttributes) {
        try {
            mantenimientoService.finalizarCompleto(
                id, descripcion,
                LocalDate.parse(fechaUltimo),
                intervalo,
                repuestos
            );
            redirectAttributes.addFlashAttribute("success", "Mantenimiento finalizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al finalizar: " + e.getMessage());
        }
        return "redirect:/tecnico/mismantenimientos";
    }

    // ── Ver detalle ────────────────────────────────────────────
    @GetMapping("/tecnico/historial/{id}")
    public String verDetalle(@PathVariable Long id,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null) return "redirect:/login";

        Mantenimiento detalle = mantenimientoService.getDetalle(id);

        if (detalle == null) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado.");
            return "redirect:/tecnico/historial";
        }

        model.addAttribute("detalle", detalle);
        return "tecnico/detalle";
    }
}