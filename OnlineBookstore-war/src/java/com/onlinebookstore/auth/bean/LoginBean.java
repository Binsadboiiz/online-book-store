package com.onlinebookstore.auth.bean;

import com.onlinebookstore.auth.dto.LoginRequest;
import com.onlinebookstore.auth.dto.LoginResponse;
import com.onlinebookstore.auth.service.AuthService;
import com.onlinebookstore.common.dto.ApiResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usernameOrEmail;
    private String password;
    private String errorMessage;

    @Inject
    private AuthService authService;

    @Inject
    private AuthBean authBean;

    public String login() {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorMessage = "Please enter both username/email and password.";
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
            return null;
        }

        try {
            LoginRequest request = new LoginRequest();
            request.setUsernameOrEmail(usernameOrEmail.trim());
            request.setPassword(password);

            ApiResponse<LoginResponse> response = authService.login(request);
            if (response != null && response.isSuccess() && response.getData() != null) {
                LoginResponse loginData = response.getData();
                authBean.setCurrentUser(loginData.getUser());
                authBean.setToken(loginData.getToken());

                if (authBean.isAdmin() || authBean.isManager()) {
                    return "/pages/admin/dashboard.xhtml?faces-redirect=true";
                }
                return "/pages/customer/home.xhtml?faces-redirect=true";
            } else {
                errorMessage = response != null ? response.getMessage() : "Invalid credentials";
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
                return null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage() != null ? e.getMessage() : "Authentication failed";
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
            return null;
        }
    }

    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
