package com.onlinebookstore.common.security;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

public class UserSecurityContext implements SecurityContext {
    private final UserPrincipal principal;
    private final boolean isSecure;

    public UserSecurityContext(UserPrincipal principal, boolean isSecure) {
        this.principal = principal;
        this.isSecure = isSecure;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        if (principal == null || principal.getRole() == null || role == null) {
            return false;
        }
        return principal.getRole().equalsIgnoreCase(role.trim());
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
