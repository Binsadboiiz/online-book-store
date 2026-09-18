package com.onlinebookstore.user.service;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ConflictException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.common.security.PasswordHasher;
import com.onlinebookstore.user.dto.AdminUserRequest;
import com.onlinebookstore.user.dto.UserResponse;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserService {

    @Inject
    private IUserRepository userRepository;

    public ApiResponse<List<UserResponse>> getUsers(String search, String role) {
        List<Users> users = userRepository.findAll();
        if (users == null) {
            return ApiResponse.success("Users retrieved", List.of());
        }

        List<UserResponse> result = users.stream()
                .filter(u -> {
                    if (search != null && !search.trim().isEmpty()) {
                        String q = search.trim().toLowerCase();
                        boolean matchName = u.getFullName() != null && u.getFullName().toLowerCase().contains(q);
                        boolean matchUser = u.getUsername() != null && u.getUsername().toLowerCase().contains(q);
                        boolean matchEmail = u.getEmail() != null && u.getEmail().toLowerCase().contains(q);
                        if (!matchName && !matchUser && !matchEmail) {
                            return false;
                        }
                    }
                    if (role != null && !role.trim().isEmpty() && !"ALL".equalsIgnoreCase(role.trim())) {
                        if (u.getRole() == null || !u.getRole().equalsIgnoreCase(role.trim())) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Users retrieved successfully", result);
    }

    public ApiResponse<UserResponse> getUserById(Integer id) {
        Users user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        return ApiResponse.success("User retrieved successfully", toUserResponse(user));
    }

    public ApiResponse<UserResponse> createUser(AdminUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (request.getPassword() == null || !request.getPassword().matches("^(?=.*[A-Z]).{8,}$")) {
            throw new BadRequestException("Password must be at least 8 characters long and contain at least 1 uppercase letter");
        }
        if (request.getFullName() != null && request.getFullName().matches(".*[0-9].*")) {
            throw new BadRequestException("Full name cannot contain numbers");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ConflictException("Email already exists");
        }

        Users user = new Users();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(PasswordHasher.hash(request.getPassword()));
        user.setFullName(request.getFullName() != null ? request.getFullName().trim() : request.getUsername().trim());
        
        String assignedRole = request.getRole() != null && !request.getRole().trim().isEmpty()
                ? request.getRole().trim().toUpperCase() : "CUSTOMER";
        user.setRole(assignedRole);
        user.setIsActive(request.getActive() != null ? request.getActive() : true);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        return ApiResponse.success("User created successfully", toUserResponse(user));
    }

    public ApiResponse<UserResponse> updateUser(Integer id, AdminUserRequest request) {
        Users user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            Users existingEmailUser = userRepository.findByEmail(newEmail);
            if (existingEmailUser != null && !existingEmailUser.getId().equals(id)) {
                throw new ConflictException("Email already in use by another account");
            }
            user.setEmail(newEmail);
        }

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            if (request.getFullName().matches(".*[0-9].*")) {
                throw new BadRequestException("Full name cannot contain numbers");
            }
            user.setFullName(request.getFullName().trim());
        }

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            user.setRole(request.getRole().trim().toUpperCase());
        }

        if (request.getActive() != null) {
            user.setIsActive(request.getActive());
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (!request.getPassword().matches("^(?=.*[A-Z]).{8,}$")) {
                throw new BadRequestException("Password must be at least 8 characters long and contain at least 1 uppercase letter");
            }
            user.setPassword(PasswordHasher.hash(request.getPassword().trim()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        Users updated = userRepository.update(user);

        return ApiResponse.success("User updated successfully", toUserResponse(updated));
    }

    public ApiResponse<UserResponse> updateUserStatus(Integer id, Boolean active) {
        Users user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        if (active == null) {
            user.setIsActive(!user.getIsActive());
        } else {
            user.setIsActive(active);
        }

        user.setUpdatedAt(LocalDateTime.now());
        Users updated = userRepository.update(user);

        return ApiResponse.success("User status updated successfully", toUserResponse(updated));
    }

    public ApiResponse<String> deleteUser(Integer id) {
        Users user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        userRepository.delete(id);
        return ApiResponse.success("User deleted successfully", "User ID " + id + " has been deleted.");
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
