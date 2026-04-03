package com.proyectogaes.interceptor;
import com.proyectogaes.entity.Notificacion;
import com.proyectogaes.repository.NotificacionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class NotificacionInterceptor implements HandlerInterceptor {

    @Autowired
    private NotificacionRepository notificacionRepository;

   @Override
public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    // Solo actuamos si hay un modelo y no es una redirección
    if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
        HttpSession session = request.getSession();
        Object idObj = session.getAttribute("id_usuario");
        Object rolObj = session.getAttribute("id_rol");

        if (idObj != null) {
            Integer idUsuario = Integer.valueOf(idObj.toString());
            Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

            if (idRol == 1) {
                // Cambiado a listarTodas() para coincidir con el Repository
                modelAndView.addObject("ultimasNotificaciones", notificacionRepository.listarTodas());
                modelAndView.addObject("totalNotificaciones", notificacionRepository.count()); 
            } else {
                // Cambiado a listarPorUsuario() para coincidir con el Repository
                List<Notificacion> notis = notificacionRepository.listarPorUsuario(idUsuario);
                modelAndView.addObject("ultimasNotificaciones", notis);
                modelAndView.addObject("totalNotificaciones", notis.size());
            }
        }
    }
}
}