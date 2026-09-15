package com.onlinebookstore.common.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("navBean")
@RequestScoped
public class NavigationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public String toHome() {
        return "/pages/customer/home.xhtml?faces-redirect=true";
    }

    public String toCatalog() {
        return "/pages/customer/books.xhtml?faces-redirect=true";
    }

    public String toCart() {
        return "/pages/customer/cart.xhtml?faces-redirect=true";
    }

    public String toAdminDashboard() {
        return "/pages/admin/dashboard.xhtml?faces-redirect=true";
    }

    public String toAdminBooks() {
        return "/pages/admin/books.xhtml?faces-redirect=true";
    }

    public String toAdminUsers() {
        return "/pages/admin/users.xhtml?faces-redirect=true";
    }
}
