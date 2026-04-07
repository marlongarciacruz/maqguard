package com.proyectogaes.controller;

import com.proyectogaes.entity.Inventario;
import com.proyectogaes.entity.Mantenimiento;
import com.proyectogaes.repository.UsuarioRepository;
import com.proyectogaes.service.MantenimientoService;
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

    // Necesito el servicio de mantenimientos para la lógica y el repo de usuarios
    // para saber quién es quién
    private final MantenimientoService mantenimientoService;
    private final UsuarioRepository usuarioRepository;

    public TecnicoController(MantenimientoService mantenimientoService,
            UsuarioRepository usuarioRepository) {
        this.mantenimientoService = mantenimientoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Este método es un "salvavidas": busca el nombre del usuario logueado en la
    // base de datos y me da su ID
    private Integer getIdUsuario(Authentication authentication) {
        return usuarioRepository.findByUsername(authentication.getName())
                .map(u -> u.getId())
                .orElse(null);
    }

    // ── Dashboard ──────────────────────────────────────────────
    // La pantalla principal del técnico: le muestra cuántas tareas tiene encima y
    // cuántas le faltan
    @GetMapping("/tecnico/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null)
            return "redirect:/login"; // Si se le venció la sesión, afuera

        Integer idUsuario = getIdUsuario(authentication);
        model.addAttribute("nombreTecnico", authentication.getName());
        // Traigo los números reales del servicio para las tarjetas del diseño
        model.addAttribute("mantenimientosAsignados", mantenimientoService.countAsignados(idUsuario.longValue()));
        model.addAttribute("mantenimientosPendientes", mantenimientoService.countPendientes(idUsuario.longValue()));

        return "dashboardtecnico";
    }

    // ── Mis Mantenimientos ─────────────────────────────────────
    // La lista de "tareas por hacer" filtrada solo para el técnico que entró
    @GetMapping("/tecnico/mismantenimientos")
    public String misMantenimientos(Authentication authentication, Model model) {
        if (authentication == null)
            return "redirect:/login";

        Integer idUsuario = getIdUsuario(authentication);
        List<Mantenimiento> mantenimientos = mantenimientoService.getMisMantenimientos(idUsuario.longValue());
        model.addAttribute("mantenimientos", mantenimientos);
        model.addAttribute("titulo", "Mis Tareas de Mantenimiento");

        return "tecnico/mismantenimientos";
    }

    // ── Historial ──────────────────────────────────────────────
    // Para que el técnico vea todo lo que ya terminó de arreglar anteriormente
    @GetMapping("/tecnico/historial")
    public String historial(Authentication authentication, Model model) {
        if (authentication == null)
            return "redirect:/login";

        Integer idUsuario = getIdUsuario(authentication);
        List<Mantenimiento> historial = mantenimientoService.getHistorialTecnico(idUsuario.longValue());
        model.addAttribute("historial", historial);

        return "tecnico/historialtec";
    }

    // ── Iniciar mantenimiento ──────────────────────────────────
    // Cuando el técnico le da clic a "empezar", cargo los datos de la máquina y los
    // repuestos por si gasta algo
    @GetMapping("/tecnico/mismantenimientos/{id}/iniciar")
    public String iniciar(@PathVariable Long id,
            Authentication authentication,
            Model model) {
        if (authentication == null)
            return "redirect:/login";

        Mantenimiento mantenimiento = mantenimientoService.getDetalle(id);
        List<Inventario> repuestos = mantenimientoService.getRepuestosDisponibles(); // Para que elija qué usó

        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("repuestos", repuestos);

        return "tecnico/iniciar";
    }

    // ── Finalizar mantenimiento ────────────────────────────────
    // El método más pesado: guarda qué hizo, cuándo toca el siguiente y descuenta
    // los repuestos usados
    @PostMapping("/tecnico/mismantenimientos/{id}/finalizar")
    public String finalizar(@PathVariable Long id,
            @RequestParam("descripcion_trabajo") String descripcion,
            @RequestParam("fecha_ultimo_mantenimiento") String fechaUltimo,
            @RequestParam("intervalo_mantenimiento") Integer intervalo,
            @RequestParam(value = "repuestos", required = false) Map<Long, Integer> repuestos,
            RedirectAttributes redirectAttributes) {
        try {
            // Le mando todo al servicio para que él haga el trabajo sucio de actualizar
            // tablas y stock
            mantenimientoService.finalizarCompleto(
                    id, descripcion,
                    LocalDate.parse(fechaUltimo),
                    intervalo,
                    repuestos);
            redirectAttributes.addFlashAttribute("success", "Mantenimiento finalizado correctamente.");
        } catch (Exception e) {
            // Si falla (ej. no hay stock), le aviso al técnico con un mensaje de error
            redirectAttributes.addFlashAttribute("error", "Error al finalizar: " + e.getMessage());
        }
        return "redirect:/tecnico/mismantenimientos";
    }

    // ── Ver detalle ────────────────────────────────────────────
    // Para chismosear la información completa de un mantenimiento pasado
    @GetMapping("/tecnico/historial/{id}")
    public String verDetalle(@PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (authentication == null)
            return "redirect:/login";

        Mantenimiento detalle = mantenimientoService.getDetalle(id);

        if (detalle == null) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado.");
            return "redirect:/tecnico/historial";
        }

        model.addAttribute("detalle", detalle);
        return "tecnico/detalle";
    }
}