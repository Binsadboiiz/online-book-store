package com.onlinebookstore.payment.service;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.payment.dto.CreatePaymentRequest;
import com.onlinebookstore.payment.dto.PaymentResponse;
import com.onlinebookstore.payment.dto.UpdatePaymentStatusRequest;

import java.util.List;

public interface IPaymentService {
    ApiResponse<PaymentResponse> createPayment(Integer userId, CreatePaymentRequest request);

    ApiResponse<List<PaymentResponse>> getUserPayments(Integer userId);

    ApiResponse<PaymentResponse> getPaymentById(Integer userId, Integer paymentId, boolean isManager);

    ApiResponse<PaymentResponse> getPaymentByTransactionCode(Integer userId, String transactionCode, boolean isManager);

    ApiResponse<List<PaymentResponse>> getPaymentsByOrderId(Integer userId, Integer orderId, boolean isManager);

    ApiResponse<List<PaymentResponse>> getAllPayments(String status);

    ApiResponse<PaymentResponse> updatePaymentStatus(Integer paymentId, UpdatePaymentStatusRequest request);

    ApiResponse<String> deletePayment(Integer paymentId, boolean isManager);
}


