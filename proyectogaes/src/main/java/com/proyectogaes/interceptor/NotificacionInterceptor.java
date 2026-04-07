package com.proyectogaes.interceptor;

import com.proyectogaes.entity.Notificacion;
import com.proyectogaes.repository.NotificacionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Component
public class NotificacionInterceptor implements HandlerInterceptor {

    @Autowired
    private NotificacionRepository notificacionRepository;

    // Este método se ejecuta DESPUÉS de que el controlador termina pero ANTES de
    // que se pinte el HTML
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        // Solo meto mano si hay una vista que mostrar y no es una redirección (porque
        // ahí no hay HTML que pintar)
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {

            // Reviso la sesión para saber quién está navegando en este momento
            HttpSession session = request.getSession();
            Object idObj = session.getAttribute("id_usuario");
            Object rolObj = session.getAttribute("id_rol");

            // Si el usuario está logueado, le cargo sus notificaciones automáticamente
            if (idObj != null) {
                Integer idUsuario = Integer.valueOf(idObj.toString());
                Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

                // Si es ADMIN (Rol 1), le mando todas las alertas del sistema para que esté al
                // tanto
                if (idRol == 1) {
                    modelAndView.addObject("ultimasNotificaciones", notificacionRepository.listarTodas());
                    modelAndView.addObject("totalNotificaciones", notificacionRepository.count());
                }
                // Si es un usuario normal (técnico), solo le cargo sus propios avisos
                else {
                    List<Notificacion> notis = notificacionRepository.listarPorUsuario(idUsuario);
                    modelAndView.addObject("ultimasNotificaciones", notis);
                    modelAndView.addObject("totalNotificaciones", notis.size());
                }
                // Estos objetos "ultimasNotificaciones" y "totalNotificaciones" son los que usa
                // el layout principal
            }
        }
    }
}