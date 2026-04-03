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

        // DIAGNÓSTICO: Esto aparecerá en tu terminal de VS Code/IntelliJ
        System.out.println("DEBUG - ID en Sesión: " + idObj);
        System.out.println("DEBUG - Rol en Sesión: " + rolObj);

        if (idObj == null) {
            // Si entra aquí, el problema es que el LoginSuccessHandler NO está guardando el ID
            System.out.println("ERROR: Sesión vacía, redirigiendo...");
            return "redirect:/login"; 
        }

        Integer idUsuario = Integer.valueOf(idObj.toString());
        Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

        List<Notificacion> lista = (idRol == 1) ? notificacionRepository.listarTodas() : notificacionRepository.listarPorUsuario(idUsuario);
        
        model.addAttribute("titulo", (idRol == 1) ? "Notificaciones del Sistema" : "Mis Notificaciones");
        model.addAttribute("notificaciones", lista);
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
        
        if (idObj == null) return "redirect:/login";

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