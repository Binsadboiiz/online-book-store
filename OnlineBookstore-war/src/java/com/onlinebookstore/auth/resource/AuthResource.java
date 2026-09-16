package com.onlinebookstore.auth.resource;

import com.onlinebookstore.auth.dto.ChangePasswordRequest;
import com.onlinebookstore.auth.dto.LoginRequest;
import com.onlinebookstore.auth.dto.LoginResponse;
import com.onlinebookstore.auth.dto.RegisterRequest;
import com.onlinebookstore.auth.service.AuthService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;
import com.onlinebookstore.common.security.UserPrincipal;
import com.onlinebookstore.user.dto.UpdateProfileRequest;
import com.onlinebookstore.user.dto.UserResponse;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    @Inject
    private AuthService authService;
    
    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        ApiResponse<UserResponse> result = authService.register(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
    
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        ApiResponse<LoginResponse> result = authService.login(request);
        return Response.ok(result).build();
    }
    
    @GET
    @Path("/me")
    @Secured
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<UserResponse> result = authService.getProfile(principal.getUserId());
        return Response.ok(result).build();
    }
    
    @PUT
    @Path("/me")
    @Secured
    public Response updateProfile(@Context SecurityContext securityContext, @Valid UpdateProfileRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<UserResponse> result = authService.updateProfile(principal.getUserId(), request);
        return Response.ok(result).build();
    }
    
    @POST
    @Path("/change-password")
    @Secured
    public Response changePassword(@Context SecurityContext securityContext, @Valid ChangePasswordRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<String> result = authService.changePassword(principal.getUserId(), request);
        return Response.ok(result).build();
    }
}
