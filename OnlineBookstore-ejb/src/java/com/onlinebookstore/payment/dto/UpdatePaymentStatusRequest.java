package com.onlinebookstore.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class UpdatePaymentStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Payment status is required")
    @Size(min = 1, max = 20, message = "Status must be between 1 and 20 characters")
    private String status;

    @Size(max = 100, message = "Transaction code cannot exceed 100 characters")
    private String transactionCode;

    public UpdatePaymentStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}
