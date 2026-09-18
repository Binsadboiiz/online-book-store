package com.onlinebookstore.payment.service;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ConflictException;
import com.onlinebookstore.common.exception.ForbiddenException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.order.entity.Orders;
import com.onlinebookstore.order.enums.OrderStatus;
import com.onlinebookstore.order.repository.IOrderRepository;
import com.onlinebookstore.payment.dto.CreatePaymentRequest;
import com.onlinebookstore.payment.dto.PaymentResponse;
import com.onlinebookstore.payment.dto.UpdatePaymentStatusRequest;
import com.onlinebookstore.payment.entity.Payments;
import com.onlinebookstore.payment.enums.PaymentStatus;
import com.onlinebookstore.payment.repository.IPaymentRepository;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PaymentService implements IPaymentService {

    @Inject
    private IPaymentRepository paymentRepository;

    @Inject
    private IUserRepository userRepository;

    @Inject
    private IOrderRepository orderRepository;

    @Override
    public ApiResponse<PaymentResponse> createPayment(Integer userId, CreatePaymentRequest request) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null) {
            throw new BadRequestException("Payment request data cannot be null");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Orders order = orderRepository.findById(request.getOrderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (order.getUserId() == null || !order.getUserId().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You can only make payments for your own orders");
        }

        OrderStatus orderStatus = OrderStatus.fromString(order.getStatus());
        if (orderStatus != null && orderStatus.isTerminalState()) {
            throw new BadRequestException("Cannot process payment for an order in status: " + order.getStatus());
        }

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new BadRequestException("Order is already paid");
        }

        String txnCode = request.getTransactionCode() != null && !request.getTransactionCode().trim().isEmpty()
                ? request.getTransactionCode().trim()
                : generateTransactionCode();

        Payments existing = paymentRepository.findByTransactionCode(txnCode);
        if (existing != null) {
            throw new ConflictException("Transaction code already exists");
        }

        String method = request.getPaymentMethod().trim();
        PaymentStatus initialStatus = "COD".equalsIgnoreCase(method) ? PaymentStatus.PENDING : PaymentStatus.SUCCESS;

        Date now = new Date();
        Payments payment = new Payments();
        payment.setOrderId(order);
        payment.setPaymentMethod(method);
        payment.setAmount(request.getAmount());
        payment.setTransactionCode(txnCode);
        payment.setStatus(initialStatus.name());
        payment.setPaymentTime(now);
        payment.setCreatedAt(now);

        payment = paymentRepository.save(payment);

        // Update order status if payment is completed
        if (initialStatus == PaymentStatus.SUCCESS) {
            order.setPaymentStatus("PAID");
            if (orderStatus == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.PROCESSING.name());
            }
            order.setUpdatedAt(now);
            orderRepository.update(order);
        }

        return ApiResponse.success("Payment created successfully", PaymentResponse.fromEntity(payment));
    }

    @Override
    public ApiResponse<List<PaymentResponse>> getUserPayments(Integer userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        List<Payments> payments = paymentRepository.findByUserId(userId);
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Fetched user payments successfully", responses);
    }

    @Override
    public ApiResponse<PaymentResponse> getPaymentById(Integer userId, Integer paymentId, boolean isManager) {
        if (paymentId == null) {
            throw new BadRequestException("Invalid payment ID");
        }

        Payments payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        if (!isManager && (payment.getOrderId() == null || payment.getOrderId().getUserId() == null
                || !payment.getOrderId().getUserId().getId().equals(userId))) {
            throw new ForbiddenException("Access denied: You can only view your own payment records");
        }

        return ApiResponse.success("Payment details retrieved", PaymentResponse.fromEntity(payment));
    }

    @Override
    public ApiResponse<PaymentResponse> getPaymentByTransactionCode(Integer userId, String transactionCode, boolean isManager) {
        if (transactionCode == null || transactionCode.trim().isEmpty()) {
            throw new BadRequestException("Invalid transaction code");
        }

        Payments payment = paymentRepository.findByTransactionCode(transactionCode.trim());
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        if (!isManager && (payment.getOrderId() == null || payment.getOrderId().getUserId() == null
                || !payment.getOrderId().getUserId().getId().equals(userId))) {
            throw new ForbiddenException("Access denied: You can only view your own payment records");
        }

        return ApiResponse.success("Payment details retrieved", PaymentResponse.fromEntity(payment));
    }

    @Override
    public ApiResponse<List<PaymentResponse>> getPaymentsByOrderId(Integer userId, Integer orderId, boolean isManager) {
        if (orderId == null) {
            throw new BadRequestException("Invalid order ID");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (!isManager && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            throw new ForbiddenException("Access denied: You can only view payments for your own orders");
        }

        List<Payments> payments = paymentRepository.findByOrderId(orderId);
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Fetched order payments successfully", responses);
    }

    @Override
    public ApiResponse<List<PaymentResponse>> getAllPayments(String status) {
        List<Payments> payments;
        if (status != null && !status.trim().isEmpty()) {
            payments = paymentRepository.findByStatus(status.trim());
        } else {
            payments = paymentRepository.findAll();
        }

        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Fetched all payments successfully", responses);
    }

    @Override
    public ApiResponse<PaymentResponse> updatePaymentStatus(Integer paymentId, UpdatePaymentStatusRequest request) {
        if (paymentId == null) {
            throw new BadRequestException("Invalid payment ID");
        }

        if (request == null) {
            throw new BadRequestException("Update request cannot be null");
        }

        Payments payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        PaymentStatus currentStatus = PaymentStatus.fromString(payment.getStatus());
        if (currentStatus != null && currentStatus.isFinalized()) {
            throw new BadRequestException("Payment with status '" + payment.getStatus() + "' is finalized and cannot be modified or deleted");
        }

        String newStatusStr = request.getStatus() != null ? request.getStatus().trim() : null;
        Date now = new Date();

        if (newStatusStr != null && !newStatusStr.isEmpty()) {
            PaymentStatus targetStatus = PaymentStatus.fromString(newStatusStr);
            if (targetStatus == null) {
                throw new BadRequestException("Invalid target payment status: " + newStatusStr);
            }

            if (currentStatus != null && !currentStatus.isValidTransitionTo(targetStatus)) {
                throw new BadRequestException("Invalid payment status transition from '" + currentStatus.name() + "' to '" + targetStatus.name() + "'");
            }

            payment.setStatus(targetStatus.name());

            // Synchronize with parent Order status if successful
            if (targetStatus == PaymentStatus.SUCCESS) {
                Orders order = payment.getOrderId();
                if (order != null) {
                    order.setPaymentStatus("PAID");
                    OrderStatus orderStatus = OrderStatus.fromString(order.getStatus());
                    if (orderStatus == OrderStatus.PENDING) {
                        order.setStatus(OrderStatus.PROCESSING.name());
                    }
                    order.setUpdatedAt(now);
                    orderRepository.update(order);
                }
            }
        }

        if (request.getTransactionCode() != null && !request.getTransactionCode().trim().isEmpty()) {
            payment.setTransactionCode(request.getTransactionCode().trim());
        }

        payment.setPaymentTime(now);
        Payments updatedPayment = paymentRepository.update(payment);

        return ApiResponse.success("Payment status updated successfully", PaymentResponse.fromEntity(updatedPayment));
    }

    @Override
    public ApiResponse<String> deletePayment(Integer paymentId, boolean isManager) {
        if (!isManager) {
            throw new ForbiddenException("Access denied: Only managers can delete payment records");
        }

        if (paymentId == null) {
            throw new BadRequestException("Invalid payment ID");
        }

        Payments payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        PaymentStatus currentStatus = PaymentStatus.fromString(payment.getStatus());
        if (currentStatus != null && currentStatus.isFinalized()) {
            throw new BadRequestException("Finalized payment records (SUCCESS, FAILED, REFUNDED) cannot be deleted");
        }

        boolean deleted = paymentRepository.delete(paymentId);
        if (!deleted) {
            throw new BadRequestException("Failed to delete payment record");
        }

        return ApiResponse.success("Payment record deleted successfully", null);
    }

    private String generateTransactionCode() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
