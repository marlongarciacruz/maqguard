package com.proyectogaes.controller;

import com.proyectogaes.entity.Notificacion;
import com.proyectogaes.repository.NotificacionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/notificaciones") // Todas las rutas de avisos y alertas caen aquí
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    // El método principal para ver la bandeja de entrada de notificaciones
    @GetMapping
    public String index(HttpSession session, Model model) {
        // Saco el ID y el Rol de la sesión para saber quién está mirando
        Object idObj = session.getAttribute("id_usuario");
        Object rolObj = session.getAttribute("id_rol");

        // 1. Control de seguridad: si no hay sesión, lo mando al inicio para que no se
        // quede trabado en un bucle
        if (idObj == null) {
            System.out.println("DEBUG: Sesión vacía en Notificaciones, redirigiendo a /inicio para evitar bucle.");
            return "redirect:/inicio";
        }

        // Convierto los datos de la sesión a números para poder usarlos en las
        // consultas
        Integer idUsuario = Integer.valueOf(idObj.toString());
        Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

        // 2. Lógica de carga: si es Admin (Rol 1) ve todo, si no, solo lo que le toca a
        // él
        List<Notificacion> lista;
        if (idRol == 1) {
            lista = notificacionRepository.listarTodas();
            model.addAttribute("titulo", "Notificaciones del Sistema");
        } else {
            lista = notificacionRepository.listarPorUsuario(idUsuario);
            model.addAttribute("titulo", "Mis Notificaciones");
        }

        // Paso la lista al HTML y mando a la vista correspondiente
        model.addAttribute("notificaciones", lista);

        return "notificacionadmin/notificaciones";
    }

    // Ruta para marcar una sola notificación como leída usando su ID
    @GetMapping("/marcarLeida/{id}")
    public String marcarLeida(@PathVariable("id") Long id) {
        notificacionRepository.findById(id).ifPresent(n -> {
            n.setLeida(1); // Le cambio el estado a 1 (leída)
            notificacionRepository.save(n); // Guardo el cambio en la base de datos
        });
        return "redirect:/notificaciones"; // Refresco la página
    }

    // Para limpiar la bandeja: marca todo como leído de un solo golpe
    @PostMapping("/marcarTodasLeidas")
    public String marcarTodasLeidas(HttpSession session) {
        Object idObj = session.getAttribute("id_usuario");
        Object rolObj = session.getAttribute("id_rol");

        if (idObj == null)
            return "redirect:/inicio";

        Integer idUsuario = Integer.valueOf(idObj.toString());
        Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

        // Si es admin limpia global, si es usuario limpia solo lo suyo
        if (idRol == 1) {
            notificacionRepository.marcarTodasComoLeidasAdmin();
        } else {
            notificacionRepository.marcarTodasComoLeidas(idUsuario);
        }
        return "redirect:/notificaciones";
    }

    // Botón para borrar definitivamente una notificación de la lista
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        notificacionRepository.deleteById(id);
        return "redirect:/notificaciones";
    }
}