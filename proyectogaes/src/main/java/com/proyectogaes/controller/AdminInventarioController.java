package com.proyectogaes.controller;

import com.proyectogaes.entity.Inventario;
import com.proyectogaes.service.InventarioService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/inventario")
public class AdminInventarioController {

    private final InventarioService service;

    public AdminInventarioController(InventarioService service) {
        this.service = service;
    }

    // Método para evitar repetir código de la barra de navegación en cada
    // @GetMapping
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("totalNotificaciones", 0); // O service.contarNotificaciones()
        model.addAttribute("ultimasNotificaciones", List.of());
    }

    @GetMapping
    public String listarInventario(Model model) {
        // Debe coincidir con ${inventario} en el HTML
        model.addAttribute("inventario", service.listar());
        return "inventario/lista"; // Nombre de tu archivo HTML
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("inventario", new Inventario());
        return "inventario/crear"; // Asegúrate que el archivo se llame crear.html
    }

    @PostMapping("/guardar")
    public String guardarInventario(@ModelAttribute("inventario") Inventario inventario) {
        // Si el ID es null o 0, Spring Data lo tratará como nuevo
        service.guardar(inventario);
        return "redirect:/inventario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Inventario inventario = service.obtener(id);
        if (inventario == null) {
            return "redirect:/inventario";
        }
        model.addAttribute("inventario", inventario);
        return "inventario/crear"; // Reutilizamos la misma vista para crear/editar
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarInventario(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/inventario";
    }

    @GetMapping("/exportar")
    public void exportarExcel(HttpServletResponse response) throws IOException {

        List<Inventario> inventario = service.listar();

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventario");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Código pieza");
        header.createCell(3).setCellValue("Cantidad");
        header.createCell(4).setCellValue("Ubicación");
        header.createCell(5).setCellValue("Stock mínimo");

        int fila = 1;
        for (Inventario inv : inventario) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(inv.getId_repuesto());
            row.createCell(1).setCellValue(inv.getNombre());
            row.createCell(2).setCellValue(inv.getCodigo_pieza());
            row.createCell(3).setCellValue(inv.getCantidad());
            row.createCell(4).setCellValue(inv.getUbicacion_almacen());
            row.createCell(5).setCellValue(inv.getStock_minimo());
        }

        String fileName = "inventario_" + LocalDateTime.now() + ".xlsx";

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheet.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + fileName);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}