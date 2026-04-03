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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        if (usuario != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("id_usuario", usuario.getId());
            session.setAttribute("id_rol", (usuario.getRol() != null) ? usuario.getRol().getId() : 0);
            
            // LOG PARA CONFIRMAR EN CONSOLA
            System.out.println(">>> SESION INICIADA: Guardado ID " + usuario.getId() + " para " + username);
        }

        // REDIRECCIÓN MANUAL DESDE EL HANDLER
        response.sendRedirect(request.getContextPath() + "/inicio");
    }
}