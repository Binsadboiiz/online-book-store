package com.onlinebookstore.order.bean;

import com.onlinebookstore.auth.bean.AuthBean;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.service.IOrderService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("orderBean")
@ViewScoped
public class OrderBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<OrderResponse> userOrders = new ArrayList<>();
    private OrderResponse selectedOrder;

    @Inject
    private IOrderService orderService;

    @Inject
    private AuthBean authBean;

    @PostConstruct
    public void init() {
        loadUserOrders();
    }

    public void loadUserOrders() {
        if (!authBean.isLoggedIn()) {
            userOrders = new ArrayList<>();
            return;
        }
        try {
            ApiResponse<List<OrderResponse>> res = orderService.getUserOrders(authBean.getCurrentUserId());
            if (res != null && res.isSuccess() && res.getData() != null) {
                userOrders = res.getData();
            } else {
                userOrders = new ArrayList<>();
            }
        } catch (Exception e) {
            userOrders = new ArrayList<>();
        }
    }

    public void selectOrderForInvoice(Integer orderId) {
        if (orderId == null || !authBean.isLoggedIn()) return;
        try {
            ApiResponse<OrderResponse> res = orderService.getOrderById(authBean.getCurrentUserId(), orderId, authBean.isManager() || authBean.isAdmin());
            if (res != null && res.isSuccess()) {
                this.selectedOrder = res.getData();
            } else {
                this.selectedOrder = null;
            }
        } catch (Exception e) {
            this.selectedOrder = null;
        }
    }

    public void cancelOrder(Integer orderId) {
        if (!authBean.isLoggedIn() || orderId == null) return;
        try {
            ApiResponse<OrderResponse> res = orderService.cancelOrder(authBean.getCurrentUserId(), orderId, false);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order cancelled successfully.", null));
                loadUserOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, res != null ? res.getMessage() : "Failed to cancel order.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // Getters and Setters
    public List<OrderResponse> getUserOrders() {
        return userOrders;
    }

    public OrderResponse getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(OrderResponse selectedOrder) {
        this.selectedOrder = selectedOrder;
    }
}
