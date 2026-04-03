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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            HttpSession session = request.getSession();
            Object idObj = session.getAttribute("id_usuario");
            Object rolObj = session.getAttribute("id_rol");

            if (idObj != null) {
                Integer idUsuario = Integer.valueOf(idObj.toString());
                Integer idRol = (rolObj != null) ? Integer.valueOf(rolObj.toString()) : 0;

                if (idRol == 1) {
                    modelAndView.addObject("ultimasNotificaciones", notificacionRepository.listarTodas());
                    modelAndView.addObject("totalNotificaciones", notificacionRepository.count());
                } else {
                    List<Notificacion> notis = notificacionRepository.listarPorUsuario(idUsuario);
                    modelAndView.addObject("ultimasNotificaciones", notis);
                    modelAndView.addObject("totalNotificaciones", notis.size());
                }
            }
        }
    }
}