package com.onlinebookstore.order.service;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.order.dto.CreateOrderRequest;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.dto.UpdateOrderStatusRequest;

import java.util.List;

public interface IOrderService {
    ApiResponse<OrderResponse> createOrder(Integer userId, CreateOrderRequest request);

    ApiResponse<List<OrderResponse>> getUserOrders(Integer userId);

    ApiResponse<OrderResponse> getOrderById(Integer userId, Integer orderId, boolean isManager);

    ApiResponse<OrderResponse> getOrderByCode(Integer userId, String orderCode, boolean isManager);

    ApiResponse<OrderResponse> cancelOrder(Integer userId, Integer orderId, boolean isManager);

    ApiResponse<List<OrderResponse>> getAllOrders(String status);

    ApiResponse<OrderResponse> updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request);

    ApiResponse<String> deleteOrder(Integer orderId, boolean isManager);
}

