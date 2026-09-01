/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author ngnph
 */
public class LoginRequest {
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;
    @NotBlank(message = "Password is required")
    private String password;
    
    public LoginRequest() {
        
    }

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
    
}
