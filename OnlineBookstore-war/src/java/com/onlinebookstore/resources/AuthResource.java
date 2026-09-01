/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.resources;

import com.onlinebookstore.dto.request.LoginRequest;
import com.onlinebookstore.dto.request.RegisterRequest;
import com.onlinebookstore.dto.response.ApiResponse;
import com.onlinebookstore.dto.response.UserResponse;
import com.onlinebookstore.services.AuthService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


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
        
        if(!result.isSuccess()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
        }
        
        return Response
                .status(Response.Status.CREATED)
                .entity(result)
                .build();
    }
    
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        ApiResponse<UserResponse> result = authService.login(request);
        
        if(!result.isSuccess()) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(result)
                    .build();
        }
        
        return Response.ok(result).build();
    }
}
