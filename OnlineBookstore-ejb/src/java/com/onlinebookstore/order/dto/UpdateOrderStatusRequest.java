package com.onlinebookstore.order.dto;

import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class UpdateOrderStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 30, message = "Status must be at most 30 characters")
    private String status;

    @Size(max = 20, message = "Payment status must be at most 20 characters")
    private String paymentStatus;

    @Size(max = 100, message = "Recipient name must be at most 100 characters")
    private String recipientName;

    @Size(max = 20, message = "Recipient phone must be at most 20 characters")
    private String recipientPhone;

    @Size(max = 255, message = "Shipping address must be at most 255 characters")
    private String shippingAddress;

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;

    public UpdateOrderStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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
}
