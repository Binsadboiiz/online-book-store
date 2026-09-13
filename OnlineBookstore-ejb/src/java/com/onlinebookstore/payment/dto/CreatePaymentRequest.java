package com.onlinebookstore.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

public class CreatePaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Order ID is required")
    private Integer orderId;

    @NotNull(message = "Payment method is required")
    @Size(min = 1, max = 30, message = "Payment method must be between 1 and 30 characters")
    private String paymentMethod;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 100, message = "Transaction code cannot exceed 100 characters")
    private String transactionCode;

    public CreatePaymentRequest() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}
