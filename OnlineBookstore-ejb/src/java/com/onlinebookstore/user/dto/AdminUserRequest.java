package com.onlinebookstore.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminUserRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @jakarta.validation.constraints.Pattern(regexp = "^(?=.*[A-Z]).{8,}$", message = "Password must be at least 8 characters long and contain at least 1 uppercase letter")
    private String password;

    @NotNull(message = "Full name is required")
    @Size(min = 1, max = 100, message = "Full name must be between 1 and 100 characters")
    @jakarta.validation.constraints.Pattern(regexp = "^[^0-9]+$", message = "Full name cannot contain numbers")
    private String fullName;

    private String role; // CUSTOMER, MANAGER, ADMIN

    private Boolean active;

    public AdminUserRequest() {
    }

    public AdminUserRequest(String username, String email, String password, String fullName, String role, Boolean active) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
