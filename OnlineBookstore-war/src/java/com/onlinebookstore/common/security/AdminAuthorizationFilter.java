package com.onlinebookstore.common.security;

import com.onlinebookstore.auth.bean.AuthBean;

import jakarta.enterprise.inject.spi.CDI;
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
 * Servlet Filter enforcing strict Role-Based Access Control (RBAC) for Admin Pages (/pages/admin/*).
 * Rejects unauthenticated users and non-admin customers attempting to bypass UI controls via URL manipulation.
 */
@WebFilter(filterName = "AdminAuthorizationFilter", urlPatterns = {"/pages/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        AuthBean authBean = null;

        // Try getting AuthBean from CDI container
        try {
            authBean = CDI.current().select(AuthBean.class).get();
        } catch (Exception ignored) {
            // Fallback to HttpSession attribute if CDI lookup fails
        }

        if (authBean == null) {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                authBean = (AuthBean) session.getAttribute("authBean");
            }
        }

        boolean loggedIn = authBean != null && authBean.isLoggedIn();
        boolean hasAdminAccess = authBean != null && (authBean.isAdmin() || authBean.isManager());

        if (!loggedIn) {
            // Unauthenticated user attempting to access admin page -> redirect to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/pages/auth/login.xhtml?faces-redirect=true");
            return;
        }

        if (!hasAdminAccess) {
            // Logged in as Customer (or insufficient role) attempting to access admin page -> redirect to 403 Forbidden
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/pages/error/403.xhtml");
            return;
        }

        // User is authenticated and possesses ADMIN/MANAGER role -> proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
