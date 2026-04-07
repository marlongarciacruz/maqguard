package com.Proyecto.controller;

import com.Proyecto.domain.Rol;
import com.Proyecto.domain.Usuario;
import com.Proyecto.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        Usuario usuario = new Usuario();
        usuario.setRol(new Rol());
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", usuarioService.listarRoles());
        model.addAttribute("titulo", "Registrar Usuario");
        return "usuarios/registro";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", usuarioService.listarRoles());
            model.addAttribute("titulo", usuario.getId() == null ? "Registrar Usuario" : "Editar Usuario");
            return "usuarios/registro";
        }
        try {
            usuarioService.guardar(usuario);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", usuarioService.listarRoles());
            model.addAttribute("titulo", usuario.getId() == null ? "Registrar Usuario" : "Editar Usuario");
            return "usuarios/registro";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarEdicion(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        if (usuario.getRol() == null) {
            usuario.setRol(new Rol());
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", usuarioService.listarRoles());
        model.addAttribute("titulo", "Editar Usuario");
        return "usuarios/registro";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivarGet(@PathVariable Long id) {
        return "redirect:/usuarios";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return "redirect:/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }
}
