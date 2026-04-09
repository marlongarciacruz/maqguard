package com.proyectogaes.controller;

import com.proyectogaes.entity.Rol;
import com.proyectogaes.entity.Usuario;
import com.proyectogaes.repository.RolRepository;
import com.proyectogaes.repository.UsuarioRepository;
import com.proyectogaes.util.ExcelEstilos;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── Listar todos los usuarios ─────────────────────────────
    @GetMapping
    public String index(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/index";
    }

    // ── Mostrar formulario de creación ────────────────────────
    @GetMapping("/crear")
    public String crear(Model model) {
        List<Rol> roles = rolRepository.findAll();
        model.addAttribute("roles", roles);
        return "usuarios/crear";
    }

    // ── Guardar nuevo usuario ─────────────────────────────────
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombre,
            @RequestParam String usuario,
            @RequestParam String password,
            @RequestParam String id_rol,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        int idRol;
        try {
            idRol = Integer.parseInt(id_rol);
        } catch (NumberFormatException e) {
            idRol = id_rol.equalsIgnoreCase("Administrador") ? 1 : 2;
        }

        String estadoFinal = (estado.equals("Activo") || estado.equals("Inactivo"))
                ? estado
                : "Activo";

        Optional<Rol> rolOpt = rolRepository.findById(idRol);
        if (rolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rol no válido.");
            return "redirect:/usuarios/crear";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setUsername(usuario);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setRol(rolOpt.get());
        nuevoUsuario.setEstado(estadoFinal);

        try {
            usuarioRepository.save(nuevoUsuario);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar el usuario. Inténtalo de nuevo.");
            return "redirect:/usuarios/crear";
        }

        redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente.");
        return "redirect:/usuarios";
    }

    // ── Mostrar formulario de edición ─────────────────────────
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/usuarios";
        }
        List<Rol> roles = rolRepository.findAll();
        model.addAttribute("usuario", usuarioOpt.get());
        model.addAttribute("roles", roles);
        return "usuarios/editar";
    }

    // ── Actualizar usuario existente ──────────────────────────
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String usuario,
            @RequestParam Integer id_rol,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/usuarios";
        }

        Optional<Rol> rolOpt = rolRepository.findById(id_rol);
        if (rolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rol no válido.");
            return "redirect:/usuarios/editar/" + id;
        }

        Usuario usuarioExistente = usuarioOpt.get();
        usuarioExistente.setNombre(nombre);
        usuarioExistente.setUsername(usuario);
        usuarioExistente.setRol(rolOpt.get());
        usuarioExistente.setEstado(estado);

        usuarioRepository.save(usuarioExistente);

        redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
        return "redirect:/usuarios";
    }

    // ── Eliminar usuario ──────────────────────────────────────
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer idUsuarioLogueado = (Integer) session.getAttribute("id_usuario");
        if (idUsuarioLogueado != null && idUsuarioLogueado.equals(id)) {
            redirectAttributes.addFlashAttribute("error",
                    "No puedes eliminar tu propia cuenta de usuario. " +
                            "Pídele a otro administrador que lo haga.");
            return "redirect:/usuarios";
        }

        usuarioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente.");
        return "redirect:/usuarios";
    }

    // ── Cambiar contraseña ────────────────────────────────────
    @PostMapping("/cambiar-contrasena/{id}")
    public String cambiarContrasena(
            @PathVariable Integer id,
            @RequestParam String nueva_contrasena,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/usuarios";
        }

        usuarioOpt.get().setPassword(passwordEncoder.encode(nueva_contrasena));
        usuarioRepository.save(usuarioOpt.get());

        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        return "redirect:/usuarios";
    }

    // ── Exportar a Excel ──────────────────────────────────────
    @GetMapping("/exportar-excel")
    public void exportarExcel(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String rol,
            HttpServletResponse response) throws IOException {

        // 1. Filtrar datos
        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> nombre == null || nombre.isBlank()
                        || u.getNombre().toLowerCase().contains(nombre.toLowerCase())
                        || u.getUsername().toLowerCase().contains(nombre.toLowerCase()))
                .filter(u -> estado == null || estado.isBlank()
                        || u.getEstado().equalsIgnoreCase(estado))
                .filter(u -> rol == null || rol.isBlank()
                        || (u.getRol() != null
                                && u.getRol().getDescripcionRol().equalsIgnoreCase(rol)))
                .collect(Collectors.toList());

        // 2. Respuesta HTTP
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=MaqGuard_Usuarios_" + fechaActual + ".xlsx");

        // 3. Colores
        final String NARANJA = "F97316";
        final String NEGRO_SUAVE = "1A1A1A";
        final String BLANCO = "FFFFFF";
        final String GRIS_CLARO = "F5F5F5";
        final String GRIS_BORDE = "CCCCCC";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Usuarios");

            // ── Fila 0-1: Título ─────────────────────────────
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 4));
            Row filaLogo = sheet.createRow(0);
            filaLogo.setHeightInPoints(38);
            sheet.createRow(1).setHeightInPoints(10);

            XSSFCellStyle estiloLogo = workbook.createCellStyle();
            estiloLogo.setFillForegroundColor(new XSSFColor(ExcelEstilos.hexToBytes(NEGRO_SUAVE), null));
            estiloLogo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloLogo.setAlignment(HorizontalAlignment.CENTER);
            estiloLogo.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteLogo = workbook.createFont();
            fuenteLogo.setBold(true);
            fuenteLogo.setFontHeightInPoints((short) 16);
            fuenteLogo.setColor(new XSSFColor(ExcelEstilos.hexToBytes(NARANJA), null));
            fuenteLogo.setFontName("Arial");
            estiloLogo.setFont(fuenteLogo);
            Cell celdaLogo = filaLogo.createCell(0);
            celdaLogo.setCellValue("MAQGUARD  —  Gestión de Usuarios");
            celdaLogo.setCellStyle(estiloLogo);

            // ── Fila 2: Subtítulo + fecha + filtros ──────────
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
            Row filaModulo = sheet.createRow(2);
            filaModulo.setHeightInPoints(20);

            XSSFCellStyle estiloModulo = workbook.createCellStyle();
            estiloModulo.setFillForegroundColor(new XSSFColor(ExcelEstilos.hexToBytes(NARANJA), null));
            estiloModulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloModulo.setAlignment(HorizontalAlignment.LEFT);
            estiloModulo.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteModulo = workbook.createFont();
            fuenteModulo.setFontHeightInPoints((short) 9);
            fuenteModulo.setColor(new XSSFColor(ExcelEstilos.hexToBytes(BLANCO), null));
            fuenteModulo.setFontName("Arial");
            estiloModulo.setFont(fuenteModulo);
            Cell celdaModulo = filaModulo.createCell(0);
            celdaModulo.setCellValue("  Módulo: Gestión de Usuarios   |   "
                    + "Exportado: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + "   |   " + construirDescripcionFiltros(nombre, estado, rol));
            celdaModulo.setCellStyle(estiloModulo);

            // ── Fila 3: Separación ───────────────────────────
            sheet.createRow(3).setHeightInPoints(5);

            // ── Fila 4: Encabezados ──────────────────────────
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(22);
            String[] columnas = { "ID", "Nombre Completo", "Usuario", "Rol", "Estado" };

            XSSFCellStyle estiloHeader = workbook.createCellStyle();
            estiloHeader.setFillForegroundColor(new XSSFColor(ExcelEstilos.hexToBytes(NARANJA), null));
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloHeader.setAlignment(HorizontalAlignment.CENTER);
            estiloHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            ExcelEstilos.setBorder(estiloHeader, GRIS_BORDE);
            XSSFFont fuenteHeader = workbook.createFont();
            fuenteHeader.setBold(true);
            fuenteHeader.setFontHeightInPoints((short) 10);
            fuenteHeader.setColor(new XSSFColor(ExcelEstilos.hexToBytes(BLANCO), null));
            fuenteHeader.setFontName("Arial");
            estiloHeader.setFont(fuenteHeader);

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            // ── Estilos filas alternas ───────────────────────
            XSSFCellStyle estiloPar = ExcelEstilos.crearEstiloDato(workbook, BLANCO, NEGRO_SUAVE, GRIS_BORDE, false);
            XSSFCellStyle estiloImpar = ExcelEstilos.crearEstiloDato(workbook, GRIS_CLARO, NEGRO_SUAVE, GRIS_BORDE,
                    false);
            XSSFCellStyle estiloCentPar = ExcelEstilos.crearEstiloDato(workbook, BLANCO, NEGRO_SUAVE, GRIS_BORDE, true);
            XSSFCellStyle estiloCentImpar = ExcelEstilos.crearEstiloDato(workbook, GRIS_CLARO, NEGRO_SUAVE, GRIS_BORDE,
                    true);

            // ── Datos ────────────────────────────────────────
            int rowNum = 5;
            for (Usuario u : usuarios) {
                Row row = sheet.createRow(rowNum);
                row.setHeightInPoints(18);
                boolean par = (rowNum % 2 == 0);
                XSSFCellStyle base = par ? estiloPar : estiloImpar;
                XSSFCellStyle cent = par ? estiloCentPar : estiloCentImpar;

                String rolDisplay = "Desconocido";
                if (u.getRol() != null) {
                    String desc = u.getRol().getDescripcionRol();
                    rolDisplay = desc.substring(0, 1).toUpperCase() + desc.substring(1).toLowerCase();
                }

                row.createCell(0).setCellValue(u.getId());
                row.getCell(0).setCellStyle(cent);

                row.createCell(1).setCellValue(u.getNombre());
                row.getCell(1).setCellStyle(base);

                row.createCell(2).setCellValue(u.getUsername());
                row.getCell(2).setCellStyle(base);

                row.createCell(3).setCellValue(rolDisplay);
                row.getCell(3).setCellStyle(cent);

                row.createCell(4).setCellValue(u.getEstado());
                row.getCell(4).setCellStyle(aplicarEstiloEstado(workbook, u.getEstado(), par, GRIS_CLARO));

                rowNum++;
            }

            // ── Fila total ───────────────────────────────────
            rowNum++;
            Row filaTotales = sheet.createRow(rowNum);
            filaTotales.setHeightInPoints(18);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));

            XSSFCellStyle estiloTotalLabel = workbook.createCellStyle();
            estiloTotalLabel.setFillForegroundColor(new XSSFColor(ExcelEstilos.hexToBytes(NEGRO_SUAVE), null));
            estiloTotalLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTotalLabel.setAlignment(HorizontalAlignment.RIGHT);
            estiloTotalLabel.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteTotalLabel = workbook.createFont();
            fuenteTotalLabel.setBold(true);
            fuenteTotalLabel.setFontName("Arial");
            fuenteTotalLabel.setFontHeightInPoints((short) 10);
            fuenteTotalLabel.setColor(new XSSFColor(ExcelEstilos.hexToBytes(NARANJA), null));
            estiloTotalLabel.setFont(fuenteTotalLabel);
            Cell celdaLabel = filaTotales.createCell(0);
            celdaLabel.setCellValue("Total de usuarios exportados:");
            celdaLabel.setCellStyle(estiloTotalLabel);

            XSSFCellStyle estiloTotalNum = workbook.createCellStyle();
            estiloTotalNum.setFillForegroundColor(new XSSFColor(ExcelEstilos.hexToBytes(NARANJA), null));
            estiloTotalNum.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTotalNum.setAlignment(HorizontalAlignment.CENTER);
            estiloTotalNum.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont fuenteTotalNum = workbook.createFont();
            fuenteTotalNum.setBold(true);
            fuenteTotalNum.setFontName("Arial");
            fuenteTotalNum.setFontHeightInPoints((short) 11);
            fuenteTotalNum.setColor(new XSSFColor(ExcelEstilos.hexToBytes(BLANCO), null));
            estiloTotalNum.setFont(fuenteTotalNum);
            Cell celdaTotal = filaTotales.createCell(4);
            celdaTotal.setCellValue(usuarios.size());
            celdaTotal.setCellStyle(estiloTotalNum);

            // ── Anchos de columna ────────────────────────────
            int[] anchos = { 8, 28, 20, 18, 14 };
            for (int i = 0; i < anchos.length; i++) {
                sheet.setColumnWidth(i, anchos[i] * 256);
            }

            workbook.write(response.getOutputStream());
        }
    }

    // ── Auxiliar: color según estado ──────────────────────────
    private XSSFCellStyle aplicarEstiloEstado(XSSFWorkbook wb, String estado,
            boolean par, String grisClaro) {
        String bgHex = par ? "FFFFFF" : grisClaro;
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(new XSSFColor(ExcelEstilos.hexToBytes(bgHex), null));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        ExcelEstilos.setBorder(cs, "CCCCCC");
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        if (estado != null) {
            switch (estado.toLowerCase()) {
                case "activo" -> font.setColor(new XSSFColor(ExcelEstilos.hexToBytes("1A7A30"), null));
                case "inactivo" -> font.setColor(new XSSFColor(ExcelEstilos.hexToBytes("CC0000"), null));
                default -> font.setColor(new XSSFColor(ExcelEstilos.hexToBytes("1A1A1A"), null));
            }
        }
        cs.setFont(font);
        return cs;
    }

    // ── Auxiliar: descripción de filtros ──────────────────────
    private String construirDescripcionFiltros(String nombre, String estado, String rol) {
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
        if (rol != null && !rol.isBlank()) {
            sb.append("Rol=\"").append(rol).append("\"  ");
            alguno = true;
        }
        if (!alguno)
            sb.append("Ninguno (todos los usuarios)");
        return sb.toString().trim();
    }
}