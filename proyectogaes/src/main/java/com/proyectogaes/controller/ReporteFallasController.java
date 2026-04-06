package com.proyectogaes.controller;

import com.proyectogaes.model.ReporteFalla;
import com.proyectogaes.service.ReporteFallaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/falla")
public class ReporteFallasController {

    private final ReporteFallaService service;

    public ReporteFallasController(ReporteFallaService service) {
        this.service = service;
    }

    // LISTAR
    @GetMapping
    public String index(Model model) {
        model.addAttribute("reportes", service.listar());
        return "reportes/index";
    }

    // CREAR
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("reporte", new ReporteFalla());
        return "reportes/crear";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ReporteFalla reporte) {
        service.guardar(reporte);
        return "redirect:/falla";
    }

    // VER
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("reporte", service.obtener(id));
        return "reportes/ver";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("reporte", service.obtener(id));
        return "reportes/editar";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute ReporteFalla reporte) {
        reporte.setIdFalla(id);
        service.guardar(reporte);
        return "redirect:/falla";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/falla";
    }
}