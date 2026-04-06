package com.proyectogaes.controller;

import com.proyectogaes.entity.ReporteFalla;
import com.proyectogaes.service.ReporteFallaService;
import com.proyectogaes.service.MaquinaService; // <--- Necesitas este servicio
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication; // Para el nombre del usuario
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/falla")
public class ReporteFallasController {

    private final ReporteFallaService service;
    private final MaquinaService maquinaService; // <--- Inyectar para el modal

    public ReporteFallasController(ReporteFallaService service, MaquinaService maquinaService) {
        this.service = service;
        this.maquinaService = maquinaService;
    }

    // LISTAR
    @GetMapping
    public String index(Model model) {
        model.addAttribute("reportes", service.listar());
        return "reportes/index";
    }

    // CREAR: Se agregó la lista de máquinas para el modal
    @GetMapping("/crear")
    public String crear(Model model, Authentication auth) {
        model.addAttribute("reporte", new ReporteFalla());
        model.addAttribute("maquinas", maquinaService.listar()); // <--- Obligatorio para el modal
        
        // Enviamos el nombre del usuario logueado para la vista
        if (auth != null) {
            model.addAttribute("usuarioActual", auth.getName());
        }
        
        return "reportes/crear";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("reporte") ReporteFalla reporte, 
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("maquinas", maquinaService.listar()); // Reenviar si hay error
            return "reportes/crear";
        }
        service.guardar(reporte);
        return "redirect:/falla";
    }

    // VER
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        ReporteFalla reporte = service.obtener(id);
        if (reporte == null) return "redirect:/falla";
        model.addAttribute("reporte", reporte);
        return "reportes/ver";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ReporteFalla reporte = service.obtener(id);
        if (reporte == null) return "redirect:/falla";
        
        model.addAttribute("reporte", reporte);
        model.addAttribute("maquinas", maquinaService.listar()); // Por si cambian la máquina
        return "reportes/editar";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, 
                             @Valid @ModelAttribute("reporte") ReporteFalla reporte, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("maquinas", maquinaService.listar());
            return "reportes/editar";
        }
        
        ReporteFalla existente = service.obtener(id);
        if (existente != null) {
            reporte.setIdFalla(id);
            // IMPORTANTE: Mantener la fecha original de creación
            reporte.setFechaReporte(existente.getFechaReporte()); 
            service.guardar(reporte);
        }
        
        return "redirect:/falla";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/falla";
    }
}