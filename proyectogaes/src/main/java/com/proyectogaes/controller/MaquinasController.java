package com.proyectogaes.controller;

import com.proyectogaes.entity.Maquina;
import com.proyectogaes.repository.MaquinasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/maquinas") // Ruta base para todo el controlador
public class MaquinasController {

    @Autowired
    private MaquinasRepository repository;

    // Listar todas las máquinas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("maquinas", repository.findAll());
        return "maquinasadmin/maquinas";
    }

    // Mostrar formulario de creación
    // URL: /maquinas/crear
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("maquina", new Maquina());
        return "maquinasadmin/crear";
    }

    // Mostrar alertas de mantenimiento
    // URL: /maquinas/alertas
    @GetMapping("/alertas")
    public String verAlertas(@RequestParam(defaultValue = "5") int dias, Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        List<Maquina> alertas = repository.buscarAlertas(hoy, limite);
        model.addAttribute("alertas", alertas);
        model.addAttribute("diasSeleccionados", dias);
        return "maquinasadmin/alertas"; 
    }

    // Guardar (Crear o Actualizar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Maquina maquina, RedirectAttributes flash) {
        try {
            repository.save(maquina); 
            flash.addFlashAttribute("success", "Operación realizada con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        return "redirect:/maquinas";
    }

    // Editar máquina
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Maquina maquina = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("maquina", maquina);
        return "maquinasadmin/editar";
    }

    // Eliminar máquina
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            repository.deleteById(id);
            flash.addFlashAttribute("success", "Máquina eliminada correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar la máquina");
        }
        return "redirect:/maquinas";
    }

    // Exportación a Excel
    @GetMapping("/exportarExcel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        response.setHeader("Content-Disposition", "attachment; filename=maquinas_" + fechaActual + ".xlsx");

        List<Maquina> maquinas = repository.findAll();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Maquinas");

            // Estilo para encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] columnas = {"ID", "Nombre", "Modelo", "Ubicación", "Últ. Manto", "Intervalo", "Próx. Manto", "Estado"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 1;
            for (Maquina maq : maquinas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(maq.getId_maquina());
                row.createCell(1).setCellValue(maq.getNombre());
                row.createCell(2).setCellValue(maq.getModelo());
                row.createCell(3).setCellValue(maq.getUbicacion());
                row.createCell(4).setCellValue(maq.getFechaUltimoMantenimiento() != null ? maq.getFechaUltimoMantenimiento().toString() : "");
                row.createCell(5).setCellValue(maq.getIntervaloMantenimiento() != null ? maq.getIntervaloMantenimiento() : 0);
                row.createCell(6).setCellValue(maq.getFechaProximoMantenimiento() != null ? maq.getFechaProximoMantenimiento().toString() : "");
                row.createCell(7).setCellValue(maq.getEstado());
            }

            // Autoajustar columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}