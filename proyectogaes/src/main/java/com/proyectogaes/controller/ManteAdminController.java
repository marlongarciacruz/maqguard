package com.proyectogaes.controller;

import com.proyectogaes.entity.Mantenimiento;
import com.proyectogaes.repository.MantenimientoRepository;
import com.proyectogaes.repository.UsuarioRepository;
import com.proyectogaes.repository.MaquinasRepository;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@RequestMapping("/mantenimientos") // Base de todas las rutas para esta sección
public class ManteAdminController {

    // Inyecto los repositorios para poder consultar máquinas, usuarios y los
    // mantenimientos
    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MaquinasRepository maquinasRepository;

    // Listar todo: traigo los mantenimientos (con sus relaciones) y los usuarios
    // para los filtros
    @GetMapping
    public String listarMantenimientos(Model model) {
        model.addAttribute("mantenimientos", mantenimientoRepository.findAllConRelaciones());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "mantenimientosadmin/mantenimientos";
    }

    // Preparo el formulario para un mantenimiento nuevo, poniéndolo en "Pendiente"
    // por defecto
    @GetMapping("/crear")
    public String formularioCrear(Model model) {
        try {
            Mantenimiento mantenimiento = new Mantenimiento();
            mantenimiento.setEstado("Pendiente");

            model.addAttribute("mantenimiento", mantenimiento);
            model.addAttribute("maquinas", maquinasRepository.findAll());
            model.addAttribute("usuarios", usuarioRepository.findAll());

            return "mantenimientosadmin/crearmanteadmin";
        } catch (Exception e) {
            // Si algo truena, imprimo el error en consola para saber qué pasó
            e.printStackTrace();
            return "error";
        }
    }

    // Busco un mantenimiento específico por su ID para mandarlo a la vista de
    // edición
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido:" + id));

        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquinas", maquinasRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());

        return "mantenimientosadmin/editarmanteadmin";
    }

    // Aquí guardo o actualizo el mantenimiento y mando un mensaje de éxito o error
    // a la vista
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Mantenimiento mantenimiento, RedirectAttributes flash) {
        try {
            mantenimientoRepository.save(mantenimiento);
            flash.addFlashAttribute("success", "Mantenimiento guardado correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/mantenimientos";
    }

    // Borro el registro y aviso si se pudo o no mediante un mensaje flash
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            mantenimientoRepository.deleteById(id);
            flash.addFlashAttribute("success", "Registro eliminado correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el registro");
        }
        return "redirect:/mantenimientos";
    }

    // Genero el reporte en Excel con toda la data de los mantenimientos
    @GetMapping("/exportarExcel")
    public void exportarExcel(HttpServletResponse response) throws IOException {

        // Configuro el tipo de archivo para que sea un Excel de verdad
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // Le pego la fecha actual al nombre del archivo para llevar un orden
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        response.setHeader("Content-Disposition", "attachment; filename=mantenimientos_" + fechaActual + ".xlsx");

        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAllConRelaciones();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Historial Mantenimientos");

            // Defino los títulos de las columnas arriba del todo
            Row header = sheet.createRow(0);
            String[] columnas = { "ID", "Fecha", "Máquina", "Técnico", "Tipo", "Descripción", "Costo", "Estado" };

            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            // Empiezo a llenar las filas con la información de la base de datos
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Mantenimiento m : mantenimientos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(m.getId_mantenimiento());
                // Formateo la fecha para que no salga rara en el Excel
                row.createCell(1).setCellValue(
                        m.getFechaMantenimiento() != null ? m.getFechaMantenimiento().format(formatter) : "");
                // Valido que los objetos relacionados no vengan nulos para que no explote
                row.createCell(2).setCellValue(m.getMaquina() != null ? m.getMaquina().getNombre() : "N/A");
                row.createCell(3).setCellValue(m.getUsuario() != null ? m.getUsuario().getNombre() : "N/A");
                row.createCell(4).setCellValue(m.getTipoMantenimiento());
                row.createCell(5).setCellValue(m.getDescripcionTrabajo());
                row.createCell(6).setCellValue(m.getCostoMantenimiento() != null ? m.getCostoMantenimiento() : 0.0);
                row.createCell(7).setCellValue(m.getEstado());
            }

            // Mando el archivo al navegador del usuario
            workbook.write(response.getOutputStream());
        }
    }
}