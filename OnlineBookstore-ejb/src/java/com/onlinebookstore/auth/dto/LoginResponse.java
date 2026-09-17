package com.onlinebookstore.auth.dto;

import com.onlinebookstore.user.dto.UserResponse;

public class LoginResponse {
    private String token;
    private String sessionId;
    private String tokenType = "Bearer";
    private UserResponse user;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.sessionId = token;
        this.tokenType = "Bearer";
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
