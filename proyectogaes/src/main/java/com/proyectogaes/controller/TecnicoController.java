package com.proyectogaes.controller;

import com.proyectogaes.entity.MantenimientoDTO;
import com.proyectogaes.entity.MantenimientoDetalleDTO;
import com.proyectogaes.service.MantenimientoService;
import jakarta.servlet.http.HttpSession;
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

    public TecnicoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @GetMapping("/tecnico/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long idUsuario = (Long) session.getAttribute("id_usuario");
        String nombreTecnico = (String) session.getAttribute("nombre");

        if (idUsuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("nombreTecnico", nombreTecnico);
        model.addAttribute("mantenimientosAsignados", mantenimientoService.countAsignados(idUsuario));
        model.addAttribute("mantenimientosPendientes", mantenimientoService.countPendientes(idUsuario));

        return "dashboardtecnico"; // → templates/dashboardtecnico.html
    }

    @GetMapping("/tecnico/mismantenimientos")
    public String misMantenimientos(HttpSession session, Model model) {
        Long idUsuario = (Long) session.getAttribute("id_usuario");

        if (idUsuario == null) {
            return "redirect:/login";
        }

        List<MantenimientoDTO> mantenimientos = mantenimientoService.getMisMantenimientos(idUsuario);
        model.addAttribute("mantenimientos", mantenimientos);
        model.addAttribute("titulo", "Mis Tareas de Mantenimiento");

        return "tecnico/mismantenimientos"; // → templates/tecnico/mismantenimientos.html
    }

    @GetMapping("/tecnico/historial")
    public String historial(HttpSession session, Model model) {
        Long idUsuario = (Long) session.getAttribute("id_usuario");

        if (idUsuario == null) {
            return "redirect:/login";
        }

        List<MantenimientoDTO> historial = mantenimientoService.getHistorialTecnico(idUsuario);
        model.addAttribute("historial", historial);

        return "tecnico/historialtec"; // → templates/tecnico/historialtec.html
    }

    @GetMapping("/tecnico/mismantenimientos/{id}/iniciar")
    public String iniciar(@PathVariable Long id,
                          HttpSession session,
                          Model model) {
        Long idUsuario = (Long) session.getAttribute("id_usuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("mantenimiento", mantenimientoService.getDetalleConMaquina(id));
        model.addAttribute("maquinaInfo", mantenimientoService.getMaquinaDeMantenimiento(id));
        model.addAttribute("repuestos", mantenimientoService.getRepuestosDisponibles());

        return "tecnico/iniciar"; // → templates/tecnico/iniciar.html
    }

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
            redirectAttributes.addFlashAttribute("success",
                "Mantenimiento finalizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al finalizar: " + e.getMessage());
        }
        return "redirect:/tecnico/mismantenimientos";
    }

    @GetMapping("/tecnico/historial/{id}")
    public String verDetalle(@PathVariable Long id,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Long idUsuario = (Long) session.getAttribute("id_usuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }

        MantenimientoDetalleDTO detalle = mantenimientoService.getDetalleCompleto(id);

        if (detalle == null) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado.");
            return "redirect:/tecnico/historial";
        }

        model.addAttribute("detalle", detalle);
        return "tecnico/detalle"; // → templates/tecnico/detalle.html
    }
}