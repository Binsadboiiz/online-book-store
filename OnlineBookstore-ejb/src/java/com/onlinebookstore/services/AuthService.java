/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.services;

import com.onlinebookstore.dto.request.LoginRequest;
import com.onlinebookstore.dto.request.RegisterRequest;
import com.onlinebookstore.dto.response.ApiResponse;
import com.onlinebookstore.dto.response.UserResponse;
import com.onlinebookstore.entities.Users;
import com.onlinebookstore.repositories.interfaces.IUserRepository;
import com.onlinebookstore.security.PasswordHasher;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

/**
 *
 * @author ngnph
 */
@Stateless
public class AuthService {
    
    @Inject
    private IUserRepository userRepository;
    
    public ApiResponse<UserResponse> register(RegisterRequest request) {
        
        // Validate request
        if(request == null) return ApiResponse.failed("Request cannot be null");
        
        if(isBlank(request.getUsername())) return ApiResponse.failed("Username is required");
        
        if(isBlank(request.getEmail())) return ApiResponse.failed("Email is required");
        
        if(isBlank(request.getPassword())) return ApiResponse.failed("Password id required");
        
        if(isBlank(request.getFullName())) return ApiResponse.failed("Full name is required");
        
        // Check username
        if(userRepository.existsByUsername(request.getUsername())) return ApiResponse.failed("Username already exists");
        
        // Check email
        if(userRepository.existsByEmail(request.getEmail())) return ApiResponse.failed("Email already exists");
        
        // Create user
        Users user = new Users();
        
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(PasswordHasher.hash(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        
        user.setRole("customer");
        
        user.setIsActive(true);
        
        LocalDateTime now = LocalDateTime.now();
        
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // Save
        userRepository.save(user);
        
        // Convert entity -> response dto
        UserResponse response = toUserResponse(user);
        
        return ApiResponse.success("Registration successful", response);
    }
    
    public ApiResponse<UserResponse> login(LoginRequest request) {
        // Validate request
        if(request == null) return ApiResponse.failed("Request cannot be null");
        
        if(isBlank(request.getUsernameOrEmail())) return ApiResponse.failed("Username or Email is required");
        
        if(isBlank(request.getPassword())) return ApiResponse.failed("Password is required");
        
        String usernameOrEmail = request.getUsernameOrEmail().trim();
        
        // Find user
        Users user = userRepository.findByUsernameOrEmail(usernameOrEmail);
        
        // Check exists
        if(user == null) return ApiResponse.failed("Invalid username or password");
        
        // Check status
        if(!user.getIsActive()) return ApiResponse.failed("Account is disable");
        
        // Verify password
        if(!PasswordHasher.verify(request.getPassword(), user.getPassword())) return ApiResponse.failed("Invalid username  or password");
        
        // 5. Convert Entity -> Response DTO
        UserResponse response = toUserResponse(user);
        
        return ApiResponse.success("Login successful", response);
    }
    
    private UserResponse toUserResponse(Users user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt()
        );
    }
    
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
