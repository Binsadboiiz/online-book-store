package com.onlinebookstore.order.dto;

import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class UpdateOrderStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 30, message = "Status must be at most 30 characters")
    private String status;

    @Size(max = 20, message = "Payment status must be at most 20 characters")
    private String paymentStatus;

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
}
