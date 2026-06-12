package com.saberpro.config;

import com.saberpro.entity.Rol;
import com.saberpro.entity.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Controla el acceso por sesión y por rol según el prefijo de la URL.
 * Nadie entra sin iniciar sesión; cada zona (/admin, /coordinacion, /docente,
 * /estudiante) solo la ve su rol correspondiente.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = session != null ? (Usuario) session.getAttribute("usuario") : null;

        // No hay sesión -> al login
        if (usuario == null) {
            response.sendRedirect(ctx + "/login");
            return false;
        }

        Rol rol = usuario.getRol();

        if (uri.startsWith("/admin") && rol != Rol.ADMINISTRADOR) {
            return denegar(response, ctx);
        }
        if (uri.startsWith("/coordinacion") && rol != Rol.COORDINACION) {
            return denegar(response, ctx);
        }
        if (uri.startsWith("/docente") && rol != Rol.DOCENTE) {
            return denegar(response, ctx);
        }
        if (uri.startsWith("/estudiante") && rol != Rol.ESTUDIANTE) {
            return denegar(response, ctx);
        }

        return true;
    }

    private boolean denegar(HttpServletResponse response, String ctx) throws Exception {
        response.sendRedirect(ctx + "/acceso-denegado");
        return false;
    }
}
