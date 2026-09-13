package com.onlinebookstore.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

public class CreateOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Recipient name is required")
    @Size(min = 1, max = 100, message = "Recipient name must be between 1 and 100 characters")
    private String recipientName;

    @NotNull(message = "Recipient phone is required")
    @Size(min = 1, max = 20, message = "Recipient phone must be between 1 and 20 characters")
    private String recipientPhone;

    @NotNull(message = "Shipping address is required")
    @Size(min = 1, max = 255, message = "Shipping address must be between 1 and 255 characters")
    private String shippingAddress;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;

    @NotNull(message = "Payment method is required")
    @Size(min = 1, max = 30, message = "Payment method must be between 1 and 30 characters")
    private String paymentMethod;

    private BigDecimal shippingFee;

    public CreateOrderRequest() {
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }
}
