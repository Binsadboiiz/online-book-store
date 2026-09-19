package com.onlinebookstore.auth.bean;

import com.onlinebookstore.user.dto.UserResponse;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private UserResponse currentUser;
    private String token;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isManager() {
        return isLoggedIn() && ("manager".equalsIgnoreCase(currentUser.getRole()) || isAdmin());
    }

    public boolean isCustomer() {
        return isLoggedIn() && "customer".equalsIgnoreCase(currentUser.getRole());
    }

    public Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public String getFullName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }

    public String getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public String logout() {
        this.currentUser = null;
        this.token = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/pages/auth/login.xhtml?faces-redirect=true";
    }

    // Getters and Setters
    public UserResponse getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserResponse currentUser) {
        this.currentUser = currentUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
