package com.onlinebookstore.order.dto;

import com.onlinebookstore.order.entity.OrderItems;
import com.onlinebookstore.order.entity.Orders;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String orderCode;
    private Integer userId;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String note;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalAmount;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private Date createdAt;
    private Date updatedAt;
    private List<OrderItemResponse> items;

    public OrderResponse() {
        this.items = new ArrayList<>();
    }

    public static OrderResponse fromEntity(Orders order) {
        if (order == null) {
            return null;
        }

        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        if (order.getUserId() != null) {
            dto.setUserId(order.getUserId().getId());
        }
        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setNote(order.getNote());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> itemDtos = new ArrayList<>();
        if (order.getOrderItemsCollection() != null) {
            for (OrderItems item : order.getOrderItemsCollection()) {
                OrderItemResponse itemDto = OrderItemResponse.fromEntity(item);
                if (itemDto != null) {
                    itemDtos.add(itemDto);
                }
            }
        }
        dto.setItems(itemDtos);

        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
