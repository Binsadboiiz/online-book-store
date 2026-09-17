package com.onlinebookstore.common.security;

import com.onlinebookstore.common.dto.ApiResponse;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Extract token from Authorization header or X-Session-ID header
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String sessionIdHeader = requestContext.getHeaderString("X-Session-ID");

        String token = null;
        if (authorizationHeader != null && !authorizationHeader.trim().isEmpty()) {
            String trimmedHeader = authorizationHeader.trim();
            if (trimmedHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
                token = trimmedHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
            } else {
                token = trimmedHeader;
            }
        } else if (sessionIdHeader != null && !sessionIdHeader.trim().isEmpty()) {
            token = sessionIdHeader.trim();
        }

        if (token == null || token.isEmpty()) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization / Session ID header");
            return;
        }

        // Validate token / session ID
        if (!JwtProvider.validateToken(token)) {
            abortWithUnauthorized(requestContext, "Invalid or expired session ID");
            return;
        }

        // Extract user info from token
        Integer userId = JwtProvider.getUserIdFromToken(token);
        String username = JwtProvider.getUsernameFromToken(token);
        String role = JwtProvider.getRoleFromToken(token);

        // Build security context
        UserPrincipal principal = new UserPrincipal(userId, username, role);
        boolean isSecure = requestContext.getSecurityContext().isSecure();
        UserSecurityContext securityContext = new UserSecurityContext(principal, isSecure);

        // Attach security context to request
        requestContext.setSecurityContext(securityContext);

        // Role-based Access Control (RBAC) verification
        List<String> allowedRoles = extractAllowedRoles(resourceInfo);
        if (!allowedRoles.isEmpty()) {
            boolean hasPermission = allowedRoles.stream().anyMatch(securityContext::isUserInRole);
            if (!hasPermission) {
                abortWithForbidden(requestContext, "Access denied: Insufficient permissions");
            }
        }
    }

    private List<String> extractAllowedRoles(ResourceInfo resourceInfo) {
        if (resourceInfo == null) {
            return List.of();
        }

        Method method = resourceInfo.getResourceMethod();
        if (method != null && method.isAnnotationPresent(Secured.class)) {
            Secured secured = method.getAnnotation(Secured.class);
            return Arrays.asList(secured.value());
        }

        Class<?> resourceClass = resourceInfo.getResourceClass();
        if (resourceClass != null && resourceClass.isAnnotationPresent(Secured.class)) {
            Secured secured = resourceClass.getAnnotation(Secured.class);
            return Arrays.asList(secured.value());
        }

        return List.of();
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.failed(message))
                        .build()
        );
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity(ApiResponse.failed(message))
                        .build()
        );
    }
}
