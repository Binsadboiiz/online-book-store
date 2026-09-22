package com.onlinebookstore.user.bean;

import com.onlinebookstore.auth.bean.AuthBean;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.user.dto.AdminUserRequest;
import com.onlinebookstore.user.dto.UserResponse;
import com.onlinebookstore.user.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminUserBean")
@ViewScoped
public class AdminUserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<UserResponse> users = new ArrayList<>();
    private String searchQuery;
    private String selectedRole = "ALL";

    private AdminUserRequest userRequest = new AdminUserRequest();
    private Integer selectedUserId;

    @Inject
    private UserService userService;

    @Inject
    private AuthBean authBean;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    public void loadUsers() {
        try {
            ApiResponse<List<UserResponse>> res = userService.getUsers(searchQuery, selectedRole);
            if (res != null && res.isSuccess() && res.getData() != null) {
                users = res.getData();
            } else {
                users = new ArrayList<>();
            }
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }

    public void filterUsers() {
        loadUsers();
    }

    public void prepareAddUser() {
        selectedUserId = null;
        userRequest = new AdminUserRequest();
        userRequest.setRole("CUSTOMER");
        userRequest.setActive(true);
    }

    public void prepareEditUser(UserResponse u) {
        if (u == null) return;
        selectedUserId = u.getId();
        userRequest = new AdminUserRequest();
        userRequest.setUsername(u.getUsername());
        userRequest.setEmail(u.getEmail());
        userRequest.setFullName(u.getFullName());
        userRequest.setRole(u.getRole());
        userRequest.setActive(u.isActive());
    }

    public void saveUser() {
        if (selectedUserId != null && authBean != null && authBean.getCurrentUserId() != null
                && authBean.getCurrentUserId().equals(selectedUserId)
                && Boolean.FALSE.equals(userRequest.getActive())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot deactivate your own account.", null));
            return;
        }

        try {
            ApiResponse<UserResponse> res;
            if (selectedUserId == null) {
                res = userService.createUser(userRequest);
            } else {
                res = userService.updateUser(selectedUserId, userRequest);
            }

            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "User account saved successfully!", null));
                loadUsers();
                prepareAddUser();
            } else {
                String msg = res != null ? res.getMessage() : "Failed to save user account.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void toggleUserStatus(Integer userId) {
        if (userId == null) return;

        if (authBean != null && authBean.getCurrentUserId() != null && authBean.getCurrentUserId().equals(userId)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot deactivate your own account.", null));
            return;
        }

        try {
            ApiResponse<UserResponse> res = userService.updateUserStatus(userId, null);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "User status updated.", null));
                loadUsers();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, res != null ? res.getMessage() : "Failed to update user status.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // Getters and Setters
    public List<UserResponse> getUsers() {
        return users;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
    }

    public AdminUserRequest getUserRequest() {
        return userRequest;
    }

    public void setUserRequest(AdminUserRequest userRequest) {
        this.userRequest = userRequest;
    }

    public Integer getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(Integer selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
}
