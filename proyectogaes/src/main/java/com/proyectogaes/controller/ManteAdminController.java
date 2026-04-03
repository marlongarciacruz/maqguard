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
@RequestMapping("/mantenimientos")
public class ManteAdminController {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MaquinasRepository maquinasRepository;

    // ✅ LISTAR (ARREGLADO)
    @GetMapping
    public String listarMantenimientos(Model model) {
        model.addAttribute("mantenimientos", mantenimientoRepository.findAllConRelaciones());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "mantenimientosadmin/mantenimientos";
    }

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
            // Esto imprimirá el error real en tu consola negra
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido:" + id));

        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("maquinas", maquinasRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());

        return "mantenimientosadmin/editarmanteadmin";
    }

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

    // EXPORTAR EXCEL (sin cambios relevantes)
    @GetMapping("/exportarExcel")
    public void exportarExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        response.setHeader("Content-Disposition", "attachment; filename=mantenimientos_" + fechaActual + ".xlsx");

        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAllConRelaciones();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Historial Mantenimientos");

            Row header = sheet.createRow(0);
            String[] columnas = { "ID", "Fecha", "Máquina", "Técnico", "Tipo", "Descripción", "Costo", "Estado" };

            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Mantenimiento m : mantenimientos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(m.getId_mantenimiento());
                row.createCell(1).setCellValue(
                        m.getFechaMantenimiento() != null ? m.getFechaMantenimiento().format(formatter) : "");
                row.createCell(2).setCellValue(m.getMaquina() != null ? m.getMaquina().getNombre() : "N/A");
                row.createCell(3).setCellValue(m.getUsuario() != null ? m.getUsuario().getNombre() : "N/A");
                row.createCell(4).setCellValue(m.getTipoMantenimiento());
                row.createCell(5).setCellValue(m.getDescripcionTrabajo());
                row.createCell(6).setCellValue(m.getCostoMantenimiento() != null ? m.getCostoMantenimiento() : 0.0);
                row.createCell(7).setCellValue(m.getEstado());
            }

            workbook.write(response.getOutputStream());
        }
    }
}