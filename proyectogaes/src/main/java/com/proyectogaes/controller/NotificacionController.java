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
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping
    public String index(HttpSession session, Model model) {
        Object idObj = session.getAttribute("id_usuario");
        Object rolObj = session.getAttribute("id_rol");

        // 1. Romper el bucle de redirección infinita del login
        if (idObj == null) {
            System.out.println("DEBUG: Sesión vacía en Notificaciones, redirigiendo a /inicio para evitar bucle.");
            return "redirect:/inicio";
        }

        Integer idUsuario = Integer.valueOf(idObj.toString());
        Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

        // 2. Lógica de carga según el Rol (1 = Administrador)
        List<Notificacion> lista;
        if (idRol == 1) {
            lista = notificacionRepository.listarTodas();
            model.addAttribute("titulo", "Notificaciones del Sistema");
        } else {
            lista = notificacionRepository.listarPorUsuario(idUsuario);
            model.addAttribute("titulo", "Mis Notificaciones");
        }

        model.addAttribute("notificaciones", lista);
        
        // 3. Asegúrate de que esta carpeta y archivo existan exactamente así
        return "notificacionadmin/notificaciones";
    }

    @GetMapping("/marcarLeida/{id}")
    public String marcarLeida(@PathVariable("id") Long id) {
        notificacionRepository.findById(id).ifPresent(n -> {
            n.setLeida(1);
            notificacionRepository.save(n);
        });
        return "redirect:/notificaciones";
    }

    @PostMapping("/marcarTodasLeidas")
    public String marcarTodasLeidas(HttpSession session) {
        Object idObj = session.getAttribute("id_usuario");
        Object rolObj = session.getAttribute("id_rol");

        if (idObj == null) return "redirect:/inicio";

        Integer idUsuario = Integer.valueOf(idObj.toString());
        Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

        if (idRol == 1) {
            notificacionRepository.marcarTodasComoLeidasAdmin();
        } else {
            notificacionRepository.marcarTodasComoLeidas(idUsuario);
        }
        return "redirect:/notificaciones";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        notificacionRepository.deleteById(id);
        return "redirect:/notificaciones";
    }
}