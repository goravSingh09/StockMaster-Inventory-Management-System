package com.stockmaster.config;

import com.stockmaster.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow static resources, login, and error pages without authentication
        if (uri.startsWith("/login") ||
            uri.startsWith("/logout") ||
            uri.startsWith("/css/") ||
            uri.startsWith("/js/") ||
            uri.startsWith("/images/") ||
            uri.startsWith("/error") ||
            uri.startsWith("/api/public/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AdminAuthService.SESSION_ADMIN_KEY) != null) {
            return true; // Authenticated
        }

        // Unauthenticated access -> redirect to login with original target URL saved
        response.sendRedirect(request.getContextPath() + "/login?redirect=" + uri);
        return false;
    }
}
