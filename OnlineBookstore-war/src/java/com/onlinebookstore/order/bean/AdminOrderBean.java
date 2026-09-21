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
import java.util.stream.Collectors;

@Named("adminOrderBean")
@ViewScoped
public class AdminOrderBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<OrderResponse> allOrders = new ArrayList<>();
    private List<OrderResponse> filteredOrders = new ArrayList<>();

    private String selectedStatus = "";
    private String searchQuery = "";

    private OrderResponse selectedOrder;

    // Fields for Edit Order Modal
    private Integer editOrderId;
    private String editRecipientName;
    private String editRecipientPhone;
    private String editShippingAddress;
    private String editNote;
    private String editStatus;
    private String editPaymentStatus;

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
                allOrders = res.getData();
            } else {
                allOrders = new ArrayList<>();
            }
        } catch (Exception e) {
            allOrders = new ArrayList<>();
        }
        applyFilter();
    }

    public void filterOrders() {
        loadOrders();
    }

    public void applyFilter() {
        if (allOrders == null) {
            filteredOrders = new ArrayList<>();
            return;
        }

        filteredOrders = allOrders.stream()
                .filter(o -> {
                    // Filter by status dropdown
                    if (selectedStatus != null && !selectedStatus.trim().isEmpty()) {
                        if (!selectedStatus.trim().equalsIgnoreCase(o.getStatus())) {
                            return false;
                        }
                    }
                    // Filter by search query (Order code, recipient name, phone)
                    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                        String query = searchQuery.trim().toLowerCase();
                        boolean matchCode = o.getOrderCode() != null && o.getOrderCode().toLowerCase().contains(query);
                        boolean matchName = o.getRecipientName() != null && o.getRecipientName().toLowerCase().contains(query);
                        boolean matchPhone = o.getRecipientPhone() != null && o.getRecipientPhone().toLowerCase().contains(query);
                        if (!matchCode && !matchName && !matchPhone) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void selectOrderDetails(OrderResponse order) {
        this.selectedOrder = order;
    }

    public void prepareEditOrder(OrderResponse order) {
        if (order == null) return;
        this.selectedOrder = order;
        this.editOrderId = order.getId();
        this.editRecipientName = order.getRecipientName();
        this.editRecipientPhone = order.getRecipientPhone();
        this.editShippingAddress = order.getShippingAddress();
        this.editNote = order.getNote();
        this.editStatus = order.getStatus();
        this.editPaymentStatus = order.getPaymentStatus();
    }

    public void saveOrderDetails() {
        if (editOrderId == null) return;

        try {
            UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
            req.setStatus(editStatus);
            req.setPaymentStatus(editPaymentStatus);
            req.setRecipientName(editRecipientName);
            req.setRecipientPhone(editRecipientPhone);
            req.setShippingAddress(editShippingAddress);
            req.setNote(editNote);

            ApiResponse<OrderResponse> res = orderService.updateOrderStatus(editOrderId, req);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Order #" + editOrderId + " updated successfully."));
                loadOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", res != null ? res.getMessage() : "Failed to update order."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void updateStatus(Integer orderId, String newStatus) {
        if (orderId == null || newStatus == null) return;
        try {
            UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
            req.setStatus(newStatus);

            ApiResponse<OrderResponse> res = orderService.updateOrderStatus(orderId, req);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Order status updated to " + newStatus));
                loadOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", res != null ? res.getMessage() : "Failed to update order status."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteOrder(Integer orderId) {
        if (orderId == null) return;
        try {
            ApiResponse<String> res = orderService.deleteOrder(orderId, true);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Order #" + orderId + " deleted successfully."));
                loadOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", res != null ? res.getMessage() : "Failed to delete order."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // Getters and Setters
    public List<OrderResponse> getOrders() {
        return filteredOrders;
    }

    public List<OrderResponse> getAllOrders() {
        return allOrders;
    }

    public String getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public OrderResponse getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(OrderResponse selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public Integer getEditOrderId() {
        return editOrderId;
    }

    public void setEditOrderId(Integer editOrderId) {
        this.editOrderId = editOrderId;
    }

    public String getEditRecipientName() {
        return editRecipientName;
    }

    public void setEditRecipientName(String editRecipientName) {
        this.editRecipientName = editRecipientName;
    }

    public String getEditRecipientPhone() {
        return editRecipientPhone;
    }

    public void setEditRecipientPhone(String editRecipientPhone) {
        this.editRecipientPhone = editRecipientPhone;
    }

    public String getEditShippingAddress() {
        return editShippingAddress;
    }

    public void setEditShippingAddress(String editShippingAddress) {
        this.editShippingAddress = editShippingAddress;
    }

    public String getEditNote() {
        return editNote;
    }

    public void setEditNote(String editNote) {
        this.editNote = editNote;
    }

    public String getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(String editStatus) {
        this.editStatus = editStatus;
    }

    public String getEditPaymentStatus() {
        return editPaymentStatus;
    }

    public void setEditPaymentStatus(String editPaymentStatus) {
        this.editPaymentStatus = editPaymentStatus;
    }
}
