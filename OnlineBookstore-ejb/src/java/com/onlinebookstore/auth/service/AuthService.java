package com.onlinebookstore.auth.service;

import com.onlinebookstore.auth.dto.ChangePasswordRequest;
import com.onlinebookstore.auth.dto.LoginRequest;
import com.onlinebookstore.auth.dto.LoginResponse;
import com.onlinebookstore.auth.dto.RegisterRequest;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ConflictException;
import com.onlinebookstore.common.exception.ForbiddenException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.common.exception.UnauthorizedException;
import com.onlinebookstore.common.security.JwtProvider;
import com.onlinebookstore.common.security.PasswordHasher;
import com.onlinebookstore.user.dto.UpdateProfileRequest;
import com.onlinebookstore.user.dto.UserResponse;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@Stateless
public class AuthService {
    
    @Inject
    private IUserRepository userRepository;
    
    public ApiResponse<UserResponse> register(RegisterRequest request) {
        
        // Check username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        
        // Check email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        
        // Create user
        Users user = new Users();
        
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(PasswordHasher.hash(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole("CUSTOMER");
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
    
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        
        String usernameOrEmail = request.getUsernameOrEmail().trim();
        
        // Find user
        Users user = userRepository.findByUsernameOrEmail(usernameOrEmail);
        
        // Check exists
        if (user == null) {
            throw new UnauthorizedException("Invalid username or password");
        }
        
        // Check status
        if (!user.getIsActive()) {
            throw new ForbiddenException("Account is disabled");
        }
        
        // Verify password
        if (!PasswordHasher.verify(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        
        // Generate JWT Token
        String token = JwtProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        UserResponse userResponse = toUserResponse(user);
        LoginResponse loginResponse = new LoginResponse(token, userResponse);
        
        return ApiResponse.success("Login successful", loginResponse);
    }
    
    public ApiResponse<UserResponse> getProfile(Integer userId) {
        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return ApiResponse.success("User profile retrieved", toUserResponse(user));
    }
    
    public ApiResponse<UserResponse> updateProfile(Integer userId, UpdateProfileRequest request) {
        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        String newEmail = request.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new ConflictException("Email is already in use by another account");
        }
        
        user.setFullName(request.getFullName().trim());
        user.setEmail(newEmail);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.update(user);
        
        return ApiResponse.success("Profile updated successfully", toUserResponse(user));
    }
    
    public ApiResponse<String> changePassword(Integer userId, ChangePasswordRequest request) {
        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        if (!PasswordHasher.verify(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        user.setPassword(PasswordHasher.hash(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.update(user);
        
        return ApiResponse.success("Password changed successfully", null);
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
}
