package com.proyectogaes.controller;

import com.proyectogaes.entity.ReporteFalla;
import com.proyectogaes.service.ReporteFallaService;
import com.proyectogaes.service.MaquinaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/falla") // Todas las rutas de fallas técnicas empiezan por aquí
public class ReporteFallasController {

    // Traigo los servicios de fallas y máquinas porque los voy a necesitar para los
    // formularios
    private final ReporteFallaService service;
    private final MaquinaService maquinaService;

    public ReporteFallasController(ReporteFallaService service, MaquinaService maquinaService) {
        this.service = service;
        this.maquinaService = maquinaService;
    }

    // LISTAR: Pido todos los reportes registrados y los mando a la tabla principal
    @GetMapping
    public String index(Model model) {
        model.addAttribute("reportes", service.listar());
        return "reportes/index";
    }

    // CREAR: Preparo el formulario. Ojo que aquí mando la lista de máquinas para
    // que aparezcan en el modal/select
    @GetMapping("/crear")
    public String crear(Model model, Authentication auth) {
        model.addAttribute("reporte", new ReporteFalla());
        model.addAttribute("maquinas", maquinaService.listar());

        // Si el usuario está logueado, saco su nombre para que el reporte sepa quién lo
        // está creando
        if (auth != null) {
            model.addAttribute("usuarioActual", auth.getName());
        }

        return "reportes/crear";
    }

    // GUARDAR: Aquí recibo los datos. Si el usuario se saltó una validación (campo
    // vacío, etc.), lo devuelvo al formulario
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("reporte") ReporteFalla reporte,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Si hay error, hay que volver a cargar las máquinas o el select saldrá vacío
            model.addAttribute("maquinas", maquinaService.listar());
            return "reportes/crear";
        }
        service.guardar(reporte);
        return "redirect:/falla";
    }

    // VER: Una ruta rápida para consultar los detalles de una falla específica por
    // su ID
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        ReporteFalla reporte = service.obtener(id);
        if (reporte == null)
            return "redirect:/falla";
        model.addAttribute("reporte", reporte);
        return "reportes/ver";
    }

    // EDITAR: Busco la falla para cargar sus datos actuales en el formulario de
    // edición
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ReporteFalla reporte = service.obtener(id);
        if (reporte == null)
            return "redirect:/falla";

        model.addAttribute("reporte", reporte);
        model.addAttribute("maquinas", maquinaService.listar()); // Por si necesitan corregir la máquina asignada
        return "reportes/editar";
    }

    // ACTUALIZAR: Similar al guardar, pero aquí me aseguro de no perder la fecha en
    // que se creó originalmente el reporte
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
            @Valid @ModelAttribute("reporte") ReporteFalla reporte,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("maquinas", maquinaService.listar());
            return "reportes/editar";
        }

        // Primero verifico que exista, le clavo el ID y mantengo la fecha de creación
        // original para no alterar el historial
        ReporteFalla existente = service.obtener(id);
        if (existente != null) {
            reporte.setIdFalla(id);
            reporte.setFechaReporte(existente.getFechaReporte());
            service.guardar(reporte);
        }

        return "redirect:/falla";
    }

    // ELIMINAR: Borra el reporte de la falla y me devuelve a la lista
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/falla";
    }
}