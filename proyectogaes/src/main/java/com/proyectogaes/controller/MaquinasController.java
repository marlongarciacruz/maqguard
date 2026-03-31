package com.proyectogaes.controller;

import com.proyectogaes.entity.Maquina;
import com.proyectogaes.repository.MaquinasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/maquinas")
public class MaquinasController {

    @Autowired
    private MaquinasRepository repository; // Este es el nombre oficial que usaremos

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("maquinas", repository.findAll());
        return "maquinasadmin/maquinas";
    }

    @GetMapping("/alertas")
    public String verAlertas(@RequestParam(defaultValue = "5") int dias, Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        List<Maquina> alertas = repository.buscarAlertas(hoy, limite);
        model.addAttribute("alertas", alertas);
        return "maquinasadmin/alertas"; 
    }

    // Unificamos el método guardar (Sirve para crear y editar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Maquina maquina) {
        repository.save(maquina); 
        return "redirect:/maquinas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        // CAMBIO: Usamos 'repository' para que coincida con la línea 32
        Maquina maquina = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("maquina", maquina);
        return "maquinasadmin/editar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        repository.deleteById(id);
        return "redirect:/maquinas";
    }

    @GetMapping("/exportarExcel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fechaActual = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setHeader("Content-Disposition", "attachment; filename=maquinas_" + fechaActual + ".xlsx");

        List<Maquina> maquinas = repository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Maquinas");

        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] columnas = {"ID", "Nombre", "Modelo", "Ubicacion", "Ult. Manto", "Intervalo", "Prox. Manto", "Estado"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
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

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    
}