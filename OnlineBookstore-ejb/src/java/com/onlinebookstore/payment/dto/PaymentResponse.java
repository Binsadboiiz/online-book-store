package com.onlinebookstore.payment.dto;

import com.onlinebookstore.payment.entity.Payments;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PaymentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer orderId;
    private String orderCode;
    private String transactionCode;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private Date paymentTime;
    private Date createdAt;

    public PaymentResponse() {
    }

    public static PaymentResponse fromEntity(Payments payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponse dto = new PaymentResponse();
        dto.setId(payment.getId());
        if (payment.getOrderId() != null) {
            dto.setOrderId(payment.getOrderId().getId());
            dto.setOrderCode(payment.getOrderId().getOrderCode());
        }
        dto.setTransactionCode(payment.getTransactionCode());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentTime(payment.getPaymentTime());
        dto.setCreatedAt(payment.getCreatedAt());

        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
