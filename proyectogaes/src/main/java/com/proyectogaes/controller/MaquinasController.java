package com.proyectogaes.controller;

import com.proyectogaes.entity.Maquina;
import com.proyectogaes.repository.MaquinasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/maquinas")
public class MaquinasController {

    @Autowired
    private MaquinasRepository repository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("maquinas", repository.findAll());
        return "maquinasadmin/maquinas";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("maquina", new Maquina());
        return "maquinasadmin/crear";
    }

    @GetMapping("/alertas")
    public String verAlertas(@RequestParam(defaultValue = "5") int dias, Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        List<Maquina> alertas = repository.buscarAlertas(hoy, limite);
        model.addAttribute("alertas", alertas);
        model.addAttribute("diasSeleccionados", dias);
        return "maquinasadmin/alertas";
    }

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

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Maquina maquina = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("maquina", maquina);
        return "maquinasadmin/editar";
    }

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

    @GetMapping("/exportarExcel")
    public void exportarExcel(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String ubicacion,
            HttpServletResponse response) throws IOException {

        // 1. Filtrar datos
        List<Maquina> maquinas = repository.findAll().stream()
                .filter(m -> nombre == null || nombre.isBlank()
                        || m.getNombre().toLowerCase().contains(nombre.toLowerCase())
                        || m.getModelo().toLowerCase().contains(nombre.toLowerCase()))
                .filter(m -> estado == null || estado.isBlank()
                        || m.getEstado().equalsIgnoreCase(estado))
                .filter(m -> ubicacion == null || ubicacion.isBlank()
                        || m.getUbicacion().equalsIgnoreCase(ubicacion))
                .collect(Collectors.toList());

        // 2. Respuesta HTTP
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=MaqGuard_Inventario_" + fechaActual + ".xlsx");

        // 3. Colores
        final String NARANJA = "F97316";
        final String NEGRO_SUAVE = "1A1A1A";
        final String BLANCO = "FFFFFF";
        final String GRIS_CLARO = "F5F5F5";
        final String GRIS_BORDE = "CCCCCC";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Inventario");

            // ── Fila 0-1: Header con nombre del sistema ──────────────────────
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 7));
            Row filaLogo = sheet.createRow(0);
            filaLogo.setHeightInPoints(38);
            sheet.createRow(1).setHeightInPoints(10);

            XSSFCellStyle estiloLogo = workbook.createCellStyle();
            estiloLogo.setFillForegroundColor(new XSSFColor(hexToBytes(NEGRO_SUAVE), null));
            estiloLogo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloLogo.setAlignment(HorizontalAlignment.CENTER);
            estiloLogo.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteLogo = workbook.createFont();
            fuenteLogo.setBold(true);
            fuenteLogo.setFontHeightInPoints((short) 16);
            fuenteLogo.setColor(new XSSFColor(hexToBytes(NARANJA), null));
            fuenteLogo.setFontName("Arial");
            estiloLogo.setFont(fuenteLogo);
            Cell celdaLogo = filaLogo.createCell(0);
            celdaLogo.setCellValue("MAQGUARD  —  Inventario de Maquinaria");
            celdaLogo.setCellStyle(estiloLogo);

            // ── Fila 2: Subtítulo módulo + fecha + filtros ───────────────────
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
            Row filaModulo = sheet.createRow(2);
            filaModulo.setHeightInPoints(20);

            XSSFCellStyle estiloModulo = workbook.createCellStyle();
            estiloModulo.setFillForegroundColor(new XSSFColor(hexToBytes(NARANJA), null));
            estiloModulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloModulo.setAlignment(HorizontalAlignment.LEFT);
            estiloModulo.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteModulo = workbook.createFont();
            fuenteModulo.setFontHeightInPoints((short) 9);
            fuenteModulo.setColor(new XSSFColor(hexToBytes(BLANCO), null));
            fuenteModulo.setFontName("Arial");
            estiloModulo.setFont(fuenteModulo);
            Cell celdaModulo = filaModulo.createCell(0);
            celdaModulo.setCellValue("  Módulo: Inventario de Maquinaria   |   "
                    + "Exportado: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + "   |   " + construirDescripcionFiltros(nombre, estado, ubicacion));
            celdaModulo.setCellStyle(estiloModulo);

            // ── Fila 3: separación ──────────────────────────────────────────
            sheet.createRow(3).setHeightInPoints(5);

            // ── Fila 4: Encabezados ─────────────────────────────────────────
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(22);
            String[] columnas = { "ID", "Nombre", "Modelo", "Ubicación",
                    "Último Manto.", "Intervalo (días)", "Próximo Manto.", "Estado" };

            XSSFCellStyle estiloHeader = workbook.createCellStyle();
            estiloHeader.setFillForegroundColor(new XSSFColor(hexToBytes(NARANJA), null));
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloHeader.setAlignment(HorizontalAlignment.CENTER);
            estiloHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorder(estiloHeader, GRIS_BORDE);
            XSSFFont fuenteHeader = workbook.createFont();
            fuenteHeader.setBold(true);
            fuenteHeader.setFontHeightInPoints((short) 10);
            fuenteHeader.setColor(new XSSFColor(hexToBytes(BLANCO), null));
            fuenteHeader.setFontName("Arial");
            estiloHeader.setFont(fuenteHeader);

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            // ── Estilos filas alternas ──────────────────────────────────────
            XSSFCellStyle estiloPar = crearEstiloDato(workbook, BLANCO, NEGRO_SUAVE, GRIS_BORDE, false);
            XSSFCellStyle estiloImpar = crearEstiloDato(workbook, GRIS_CLARO, NEGRO_SUAVE, GRIS_BORDE, false);
            XSSFCellStyle estiloCentPar = crearEstiloDato(workbook, BLANCO, NEGRO_SUAVE, GRIS_BORDE, true);
            XSSFCellStyle estiloCentImpar = crearEstiloDato(workbook, GRIS_CLARO, NEGRO_SUAVE, GRIS_BORDE, true);

            // ── Datos ───────────────────────────────────────────────────────
            int rowNum = 5;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Maquina maq : maquinas) {
                Row row = sheet.createRow(rowNum);
                row.setHeightInPoints(18);
                boolean par = (rowNum % 2 == 0);
                XSSFCellStyle base = par ? estiloPar : estiloImpar;
                XSSFCellStyle cent = par ? estiloCentPar : estiloCentImpar;

                row.createCell(0).setCellValue(maq.getId_maquina());
                row.getCell(0).setCellStyle(cent);

                row.createCell(1).setCellValue(maq.getNombre());
                row.getCell(1).setCellStyle(base);

                row.createCell(2).setCellValue(maq.getModelo());
                row.getCell(2).setCellStyle(base);

                row.createCell(3).setCellValue(maq.getUbicacion());
                row.getCell(3).setCellStyle(base);

                row.createCell(4).setCellValue(maq.getFechaUltimoMantenimiento() != null
                        ? maq.getFechaUltimoMantenimiento().format(fmt)
                        : "—");
                row.getCell(4).setCellStyle(cent);

                row.createCell(5).setCellValue(maq.getIntervaloMantenimiento() != null
                        ? maq.getIntervaloMantenimiento()
                        : 0);
                row.getCell(5).setCellStyle(cent);

                row.createCell(6).setCellValue(maq.getFechaProximoMantenimiento() != null
                        ? maq.getFechaProximoMantenimiento().format(fmt)
                        : "—");
                row.getCell(6).setCellStyle(cent);

                row.createCell(7).setCellValue(maq.getEstado());
                row.getCell(7).setCellStyle(aplicarEstiloEstado(workbook, maq.getEstado(), par, GRIS_CLARO));

                rowNum++;
            }

            // ── Fila total ──────────────────────────────────────────────────
            rowNum++;
            Row filaTotales = sheet.createRow(rowNum);
            filaTotales.setHeightInPoints(18);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));

            XSSFCellStyle estiloTotalLabel = workbook.createCellStyle();
            estiloTotalLabel.setFillForegroundColor(new XSSFColor(hexToBytes(NEGRO_SUAVE), null));
            estiloTotalLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTotalLabel.setAlignment(HorizontalAlignment.RIGHT);
            estiloTotalLabel.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteTotalLabel = workbook.createFont();
            fuenteTotalLabel.setBold(true);
            fuenteTotalLabel.setFontName("Arial");
            fuenteTotalLabel.setFontHeightInPoints((short) 10);
            fuenteTotalLabel.setColor(new XSSFColor(hexToBytes(NARANJA), null));
            estiloTotalLabel.setFont(fuenteTotalLabel);
            Cell celdaLabel = filaTotales.createCell(0);
            celdaLabel.setCellValue("Total de máquinas exportadas:");
            celdaLabel.setCellStyle(estiloTotalLabel);

            XSSFCellStyle estiloTotalNum = workbook.createCellStyle();
            estiloTotalNum.setFillForegroundColor(new XSSFColor(hexToBytes(NARANJA), null));
            estiloTotalNum.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTotalNum.setAlignment(HorizontalAlignment.CENTER);
            estiloTotalNum.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteTotalNum = workbook.createFont();
            fuenteTotalNum.setBold(true);
            fuenteTotalNum.setFontName("Arial");
            fuenteTotalNum.setFontHeightInPoints((short) 11);
            fuenteTotalNum.setColor(new XSSFColor(hexToBytes(BLANCO), null));
            estiloTotalNum.setFont(fuenteTotalNum);
            Cell celdaTotal = filaTotales.createCell(7);
            celdaTotal.setCellValue(maquinas.size());
            celdaTotal.setCellStyle(estiloTotalNum);

            // ── Anchos ─────────────────────────────────────────────────────
            int[] anchos = { 8, 24, 20, 20, 20, 16, 20, 14 };
            for (int i = 0; i < anchos.length; i++) {
                sheet.setColumnWidth(i, anchos[i] * 256);
            }

            workbook.write(response.getOutputStream());
        }
    }

    // ── Auxiliares (van fuera del método, dentro de la clase) ─────────────────

    private byte[] hexToBytes(String hex) {
        return new byte[] {
                (byte) Integer.parseInt(hex.substring(0, 2), 16),
                (byte) Integer.parseInt(hex.substring(2, 4), 16),
                (byte) Integer.parseInt(hex.substring(4, 6), 16)
        };
    }

    private void setBorder(XSSFCellStyle cs, String colorHex) {
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
    }

    private XSSFCellStyle crearEstiloDato(XSSFWorkbook wb, String bgHex, String fgHex,
            String bordeHex, boolean centrado) {
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(new XSSFColor(hexToBytes(bgHex), null));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(hexToBytes(fgHex), null));
        cs.setFont(font);
        cs.setAlignment(centrado ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(cs, bordeHex);
        return cs;
    }

    private XSSFCellStyle aplicarEstiloEstado(XSSFWorkbook wb, String estado,
            boolean par, String grisClaro) {
        String bgHex = par ? "FFFFFF" : grisClaro;
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(new XSSFColor(hexToBytes(bgHex), null));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(cs, "CCCCCC");
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        if (estado != null) {
            switch (estado.toLowerCase()) {
                case "activo" -> font.setColor(new XSSFColor(hexToBytes("1A7A30"), null));
                case "inactivo" -> font.setColor(new XSSFColor(hexToBytes("CC0000"), null));
                case "mantenimiento" -> font.setColor(new XSSFColor(hexToBytes("F97316"), null));
                default -> font.setColor(new XSSFColor(hexToBytes("1A1A1A"), null));
            }
        }
        cs.setFont(font);
        return cs;
    }

    private String construirDescripcionFiltros(String nombre, String estado, String ubicacion) {
        StringBuilder sb = new StringBuilder("Filtros: ");
        boolean alguno = false;
        if (nombre != null && !nombre.isBlank()) {
            sb.append("Búsqueda=\"").append(nombre).append("\"  ");
            alguno = true;
        }
        if (estado != null && !estado.isBlank()) {
            sb.append("Estado=\"").append(estado).append("\"  ");
            alguno = true;
        }
        if (ubicacion != null && !ubicacion.isBlank()) {
            sb.append("Ubicación=\"").append(ubicacion).append("\"  ");
            alguno = true;
        }
        if (!alguno)
            sb.append("Ninguno (todas las máquinas)");
        return sb.toString().trim();
    }
}