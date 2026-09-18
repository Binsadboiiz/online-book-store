package com.onlinebookstore.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {
    
    @NotBlank(message = "Full name is required")
    @jakarta.validation.constraints.Pattern(regexp = "^[^0-9]+$", message = "Full name cannot contain numbers")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    public UpdateProfileRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
