package com.onlinebookstore.auth.bean;

import com.onlinebookstore.auth.dto.RegisterRequest;
import com.onlinebookstore.auth.service.AuthService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.user.dto.UserResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("registerBean")
@RequestScoped
public class RegisterBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String errorMessage;

    @Inject
    private AuthService authService;

    public String register() {
        if (password == null || !password.equals(confirmPassword)) {
            errorMessage = "Passwords do not match!";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
            return null;
        }

        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username != null ? username.trim() : "");
            request.setEmail(email != null ? email.trim() : "");
            request.setPassword(password);
            request.setFullName(fullName != null ? fullName.trim() : "");

            ApiResponse<UserResponse> response = authService.register(request);
            if (response != null && response.isSuccess()) {
                FacesContext.getCurrentInstance().getExternalContext().getFlash()
                        .setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration successful! Please sign in.", null));
                return "/pages/auth/login.xhtml?faces-redirect=true";
            } else {
                errorMessage = response != null ? response.getMessage() : "Registration failed";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
                return null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage() != null ? e.getMessage() : "Registration failed";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
            return null;
        }
    }

    // Getters and Setters
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
