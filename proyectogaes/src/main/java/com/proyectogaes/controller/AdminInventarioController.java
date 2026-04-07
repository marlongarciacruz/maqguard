package com.proyectogaes.controller;

// Importo las entidades y el servicio para que la lógica de negocio funcione
import com.proyectogaes.entity.Inventario;
import com.proyectogaes.service.InventarioService;

// Librerías de Apache POI para poder generar el Excel
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Cosas de Spring para que esto sea un controlador y maneje rutas
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/inventario") // Todas las rutas de aquí empiezan con /inventario
public class AdminInventarioController {

    // Traigo el servicio pero lo dejo marcado como final para que no se mueva de
    // ahí
    private final InventarioService service;

    // Inyecto el servicio por el constructor, que es más limpio
    public AdminInventarioController(InventarioService service) {
        this.service = service;
    }

    // Este método lo uso para que las notificaciones salgan en todas las vistas sin
    // repetir código
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("totalNotificaciones", 0);
        model.addAttribute("ultimasNotificaciones", List.of());
    }

    // El index del inventario: pide la lista al servicio y la manda al HTML
    @GetMapping
    public String listarInventario(Model model) {
        model.addAttribute("inventario", service.listar());
        return "inventario/lista";
    }

    // Manda a la vista de creación con un objeto Inventario vacío
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("inventario", new Inventario());
        return "inventario/crear";
    }

    // Aquí llega la info del formulario; si es nuevo lo crea, si tiene ID lo
    // actualiza
    @PostMapping("/guardar")
    public String guardarInventario(@ModelAttribute("inventario") Inventario inventario) {
        service.guardar(inventario);
        return "redirect:/inventario"; // Vuelve a la lista para ver el cambio
    }

    // Busca un repuesto por ID para editarlo; si no existe, te saca de ahí
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Inventario inventario = service.obtener(id);
        if (inventario == null) {
            return "redirect:/inventario";
        }
        model.addAttribute("inventario", inventario);
        return "inventario/crear"; // Uso el mismo HTML de "crear" para no trabajar doble
    }

    // Ruta rápida para borrar un registro por su ID
    @GetMapping("/eliminar/{id}")
    public String eliminarInventario(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/inventario";
    }

    // El método para bajar el Excel. Es un poco más largo porque armo el archivo a
    // mano
    @GetMapping("/exportar")
    public void exportarExcel(HttpServletResponse response) throws IOException {

        // Agarro todos los datos que hay en la base de datos
        List<Inventario> inventario = service.listar();

        // Creo el libro y la hoja de Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventario");

        // Armo la fila 0 que es el encabezado con los nombres de las columnas
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Código pieza");
        header.createCell(3).setCellValue("Cantidad");
        header.createCell(4).setCellValue("Ubicación");
        header.createCell(5).setCellValue("Stock mínimo");

        // Recorro la lista y voy metiendo cada repuesto en una fila nueva
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

        // Le pongo un nombre al archivo que incluya la fecha y hora para que no se
        // repitan
        String fileName = "inventario_" + LocalDateTime.now() + ".xlsx";

        // Configuro la respuesta para que el navegador sepa que es un Excel y lo
        // descargue
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheet.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + fileName);

        // Escribo todo en el stream de salida y cierro el archivo
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}