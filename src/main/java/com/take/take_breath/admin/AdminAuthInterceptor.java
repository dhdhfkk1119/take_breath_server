package com.take.take_breath.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // 로그인 페이지는 인증 불필요
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/admin/view/login") || requestURI.startsWith("/api/admin/view/login")) {
            return true;
        }

        // 세션이 없거나 토큰이 없으면 로그인 페이지로
        if (session == null || session.getAttribute("adminToken") == null) {
            response.sendRedirect("/api/admin/view/login");
            return false;
        }

        return true;
    }
}