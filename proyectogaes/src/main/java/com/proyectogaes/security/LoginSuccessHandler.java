package com.proyectogaes.security;

import com.proyectogaes.entity.Usuario;
import com.proyectogaes.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Este método se dispara sólamente cuando el login fue exitoso
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Saco el nombre del usuario que se acaba de loguear
        String username = authentication.getName();
        // Lo busco en mi tabla para tener toda su info a mano
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        if (usuario != null) {
            // Creo una sesión nueva desde cero para que no haya basura de sesiones
            // anteriores
            HttpSession session = request.getSession(true);

            // Guardo el ID del usuario y su ROL en la sesión
            // Esto es vital para que los interceptores y controladores sepan qué mostrarle
            // después
            session.setAttribute("id_usuario", usuario.getId());
            session.setAttribute("id_rol", (usuario.getRol() != null) ? usuario.getRol().getId() : 0);

            // Un mensajito en la consola para saber que todo salió bien durante el
            // desarrollo
            System.out.println("===> SESION CREADA: ID " + usuario.getId() + " para el usuario " + username);
        }

        // Ya con la sesión lista, lo mando a la ruta "/inicio" donde se decide a qué
        // dashboard va
        response.sendRedirect(request.getContextPath() + "/inicio");
    }
}