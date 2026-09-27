package com.ewuarsoft.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Rutas públicas que no requieren autenticación
        if (path.startsWith("/login") ||
            path.startsWith("/registro") ||
            path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/images") ||
            path.equals("/favicon.ico")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return true;
        }

        // Si no está autenticado, redirigir al login
        response.sendRedirect(request.getContextPath() + "/login?error=requiere_sesion");
        return false;
    }
}
