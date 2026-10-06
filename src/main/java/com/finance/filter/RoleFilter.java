package com.finance.filter;

import com.finance.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter enforcing Role-Based Access Control (RBAC) across system routes.
 * Ensures users with role USER cannot access ADVISOR/ADMIN pages, etc.
 */
@WebFilter(urlPatterns = {"/user/*", "/advisor/*", "/admin/*"})
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());
        String role = loggedInUser.getRole();

        // Enforce role boundaries
        if (path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required.");
            return;
        }

        if (path.startsWith("/advisor/") && !"ADVISOR".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Advisor role required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
