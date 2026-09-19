package com.onlinebookstore.order.bean;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.dto.UpdateOrderStatusRequest;
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

@Named("adminOrderBean")
@ViewScoped
public class AdminOrderBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<OrderResponse> orders = new ArrayList<>();
    private String selectedStatus;
    private OrderResponse selectedOrder;

    @Inject
    private IOrderService orderService;

    @PostConstruct
    public void init() {
        loadOrders();
    }

    public void loadOrders() {
        try {
            ApiResponse<List<OrderResponse>> res = orderService.getAllOrders(selectedStatus);
            if (res != null && res.isSuccess() && res.getData() != null) {
                orders = res.getData();
            } else {
                orders = new ArrayList<>();
            }
        } catch (Exception e) {
            orders = new ArrayList<>();
        }
    }

    public void filterByStatus() {
        loadOrders();
    }

    public void updateStatus(Integer orderId, String newStatus) {
        if (orderId == null || newStatus == null) return;
        try {
            UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
            req.setStatus(newStatus);

            ApiResponse<OrderResponse> res = orderService.updateOrderStatus(orderId, req);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order status updated to " + newStatus, null));
                loadOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, res != null ? res.getMessage() : "Failed to update order status.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // Getters and Setters
    public List<OrderResponse> getOrders() {
        return orders;
    }

    public String getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public OrderResponse getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(OrderResponse selectedOrder) {
        this.selectedOrder = selectedOrder;
    }
}
