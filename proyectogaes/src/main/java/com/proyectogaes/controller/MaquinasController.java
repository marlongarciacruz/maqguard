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

    // ─── EXPORTAR EXCEL CON FILTRO MULTICRITERIO ───────────────────────────────
    @GetMapping("/exportarExcel")
    public void exportarExcel(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String estado,
            HttpServletResponse response) throws IOException {

        // 1. Obtener y filtrar datos
        List<Maquina> maquinas = repository.findAll().stream()
                .filter(m -> nombre == null || nombre.isBlank()
                        || m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(m -> modelo == null || modelo.isBlank()
                        || m.getModelo().toLowerCase().contains(modelo.toLowerCase()))
                .filter(m -> estado == null || estado.isBlank()
                        || m.getEstado().toLowerCase().contains(estado.toLowerCase()))
                .collect(Collectors.toList());

        // 2. Configurar respuesta HTTP
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=GAES_Maquinas_" + fechaActual + ".xlsx");

        // 3. Colores corporativos
        final String NARANJA    = "FF6B00";
        final String NEGRO_SUAVE = "1F1F1F";
        final String BLANCO     = "FFFFFF";
        final String GRIS_CLARO = "F5F5F5";
        final String GRIS_BORDE = "CCCCCC";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Máquinas");

            // ── Fila 0-1: Logo / Título del software ────────────────────────
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 7));
            Row filaLogo = sheet.createRow(0);
            filaLogo.setHeightInPoints(36);
            sheet.createRow(1).setHeightInPoints(36);

            XSSFCellStyle estiloLogo = workbook.createCellStyle();
            estiloLogo.setFillForegroundColor(new XSSFColor(hexToBytes(NEGRO_SUAVE), null));
            estiloLogo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloLogo.setAlignment(HorizontalAlignment.CENTER);
            estiloLogo.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteLogo = workbook.createFont();
            fuenteLogo.setBold(true);
            fuenteLogo.setFontHeightInPoints((short) 18);
            fuenteLogo.setColor(new XSSFColor(hexToBytes(NARANJA), null));
            fuenteLogo.setFontName("Arial");
            estiloLogo.setFont(fuenteLogo);
            Cell celdaLogo = filaLogo.createCell(0);
            celdaLogo.setCellValue("⚙  GAES — Sistema de Gestión de Máquinas");
            celdaLogo.setCellStyle(estiloLogo);

            // ── Fila 2: Subtítulo con módulo y fecha ────────────────────────
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
            Row filaModulo = sheet.createRow(2);
            filaModulo.setHeightInPoints(22);

            XSSFCellStyle estiloModulo = workbook.createCellStyle();
            estiloModulo.setFillForegroundColor(new XSSFColor(hexToBytes(NARANJA), null));
            estiloModulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloModulo.setAlignment(HorizontalAlignment.LEFT);
            estiloModulo.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteModulo = workbook.createFont();
            fuenteModulo.setBold(false);
            fuenteModulo.setFontHeightInPoints((short) 10);
            fuenteModulo.setColor(new XSSFColor(hexToBytes(BLANCO), null));
            fuenteModulo.setFontName("Arial");
            estiloModulo.setFont(fuenteModulo);
            Cell celdaModulo = filaModulo.createCell(0);
            String filtrosAplicados = construirDescripcionFiltros(nombre, modelo, estado);
            celdaModulo.setCellValue("  Módulo: Administración de Máquinas  |  "
                    + "Exportado: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + "  |  " + filtrosAplicados);
            celdaModulo.setCellStyle(estiloModulo);

            // ── Fila 3: separación ──────────────────────────────────────────
            sheet.createRow(3).setHeightInPoints(6);

            // ── Fila 4: Encabezados de columna ──────────────────────────────
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(20);
            String[] columnas = {"ID", "Nombre", "Modelo", "Ubicación",
                    "Último Mantenimiento", "Intervalo (días)", "Próximo Mantenimiento", "Estado"};

            XSSFCellStyle estiloHeader = workbook.createCellStyle();
            estiloHeader.setFillForegroundColor(new XSSFColor(hexToBytes(NARANJA), null));
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloHeader.setAlignment(HorizontalAlignment.CENTER);
            estiloHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorder(estiloHeader, GRIS_BORDE);
            XSSFFont fuenteHeader = workbook.createFont();
            fuenteHeader.setBold(true);
            fuenteHeader.setFontHeightInPoints((short) 11);
            fuenteHeader.setColor(new XSSFColor(hexToBytes(BLANCO), null));
            fuenteHeader.setFontName("Arial");
            estiloHeader.setFont(fuenteHeader);

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            // ── Estilos de filas de datos (alternas) ────────────────────────
            XSSFCellStyle estiloPar      = crearEstiloDato(workbook, BLANCO,     NEGRO_SUAVE, GRIS_BORDE, false);
            XSSFCellStyle estiloImpar    = crearEstiloDato(workbook, GRIS_CLARO, NEGRO_SUAVE, GRIS_BORDE, false);
            XSSFCellStyle estiloCentPar  = crearEstiloDato(workbook, BLANCO,     NEGRO_SUAVE, GRIS_BORDE, true);
            XSSFCellStyle estiloCentImpar= crearEstiloDato(workbook, GRIS_CLARO, NEGRO_SUAVE, GRIS_BORDE, true);

            // ── Datos ────────────────────────────────────────────────────────
            int rowNum = 5;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Maquina maq : maquinas) {
                Row row = sheet.createRow(rowNum);
                row.setHeightInPoints(18);
                boolean par = (rowNum % 2 == 0);
                XSSFCellStyle estiloBase = par ? estiloPar    : estiloImpar;
                XSSFCellStyle estiloCent = par ? estiloCentPar : estiloCentImpar;

                Cell c0 = row.createCell(0);
                c0.setCellValue(maq.getId_maquina());
                c0.setCellStyle(estiloCent);

                Cell c1 = row.createCell(1);
                c1.setCellValue(maq.getNombre());
                c1.setCellStyle(estiloBase);

                Cell c2 = row.createCell(2);
                c2.setCellValue(maq.getModelo());
                c2.setCellStyle(estiloBase);

                Cell c3 = row.createCell(3);
                c3.setCellValue(maq.getUbicacion());
                c3.setCellStyle(estiloBase);

                Cell c4 = row.createCell(4);
                c4.setCellValue(maq.getFechaUltimoMantenimiento() != null
                        ? maq.getFechaUltimoMantenimiento().format(fmt) : "—");
                c4.setCellStyle(estiloCent);

                Cell c5 = row.createCell(5);
                c5.setCellValue(maq.getIntervaloMantenimiento() != null
                        ? maq.getIntervaloMantenimiento() : 0);
                c5.setCellStyle(estiloCent);

                Cell c6 = row.createCell(6);
                c6.setCellValue(maq.getFechaProximoMantenimiento() != null
                        ? maq.getFechaProximoMantenimiento().format(fmt) : "—");
                c6.setCellStyle(estiloCent);

                Cell c7 = row.createCell(7);
                c7.setCellValue(maq.getEstado());
                c7.setCellStyle(aplicarEstiloEstado(workbook, maq.getEstado(), par, GRIS_CLARO));

                rowNum++;
            }

            // ── Fila de totales ──────────────────────────────────────────────
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

            // ── Anchos de columna fijos ──────────────────────────────────────
            int[] anchos = {10, 25, 20, 22, 22, 18, 22, 15};
            for (int i = 0; i < anchos.length; i++) {
                sheet.setColumnWidth(i, anchos[i] * 256);
            }

            workbook.write(response.getOutputStream());
        }
    }

    // ── Métodos auxiliares ────────────────────────────────────────────────────

    private byte[] hexToBytes(String hex) {
        return new byte[]{
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
                case "activo"        -> font.setColor(new XSSFColor(hexToBytes("1A7A30"), null));
                case "inactivo"      -> font.setColor(new XSSFColor(hexToBytes("CC0000"), null));
                case "mantenimiento" -> font.setColor(new XSSFColor(hexToBytes("FF6B00"), null));
                default              -> font.setColor(new XSSFColor(hexToBytes("1F1F1F"), null));
            }
        }
        cs.setFont(font);
        return cs;
    }

    private String construirDescripcionFiltros(String nombre, String modelo, String estado) {
        StringBuilder sb = new StringBuilder("Filtros: ");
        boolean alguno = false;
        if (nombre != null && !nombre.isBlank()) {
            sb.append("Nombre=\"").append(nombre).append("\"  ");
            alguno = true;
        }
        if (modelo != null && !modelo.isBlank()) {
            sb.append("Modelo=\"").append(modelo).append("\"  ");
            alguno = true;
        }
        if (estado != null && !estado.isBlank()) {
            sb.append("Estado=\"").append(estado).append("\"  ");
            alguno = true;
        }
        if (!alguno) sb.append("Ninguno (todas las máquinas)");
        return sb.toString().trim();
    }
}