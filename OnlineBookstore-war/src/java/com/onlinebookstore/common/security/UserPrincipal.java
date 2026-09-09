package com.onlinebookstore.common.security;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final Integer userId;
    private final String username;
    private final String role;

    public UserPrincipal(Integer userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getName() {
        return username;
    }
}
