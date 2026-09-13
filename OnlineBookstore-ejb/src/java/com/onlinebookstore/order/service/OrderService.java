package com.onlinebookstore.order.service;

import com.onlinebookstore.book.entity.Books;
import com.onlinebookstore.book.repository.IBookRepository;
import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;
import com.onlinebookstore.cart.repository.ICartRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.order.dto.CreateOrderRequest;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.dto.UpdateOrderStatusRequest;
import com.onlinebookstore.order.entity.OrderItems;
import com.onlinebookstore.order.entity.Orders;
import com.onlinebookstore.order.repository.IOrderRepository;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class OrderService implements IOrderService {

    @Inject
    private IOrderRepository orderRepository;

    @Inject
    private ICartRepository cartRepository;

    @Inject
    private IBookRepository bookRepository;

    @Inject
    private IUserRepository userRepository;

    @Override
    public ApiResponse<OrderResponse> createOrder(Integer userId, CreateOrderRequest request) {
        if (userId == null) {
            return ApiResponse.failed("Invalid user ID");
        }

        if (request == null) {
            return ApiResponse.failed("Order request data cannot be null");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            return ApiResponse.failed("User not found");
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getCartItemsCollection() == null || cart.getCartItemsCollection().isEmpty()) {
            return ApiResponse.failed("Cart is empty");
        }

        // Validate stock availability for all cart items
        for (CartItems item : cart.getCartItemsCollection()) {
            Books book = item.getBookId();
            if (book == null || !Boolean.TRUE.equals(book.getIsActive())) {
                return ApiResponse.failed("Book '" + (book != null ? book.getTitle() : "Unknown") + "' is inactive or no longer available");
            }
            if (item.getQuantity() > book.getStockQuantity()) {
                return ApiResponse.failed("Quantity for '" + book.getTitle() + "' exceeds available stock (" + book.getStockQuantity() + ")");
            }
        }

        // Calculate order totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItems item : cart.getCartItemsCollection()) {
            Books book = item.getBookId();
            BigDecimal unitPrice = book.getDiscountPrice() != null ? book.getDiscountPrice() : book.getPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemSubtotal);
        }

        BigDecimal shippingFee = request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount.add(shippingFee);

        String orderCode = generateOrderCode();

        Date now = new Date();
        Orders order = new Orders();
        order.setOrderCode(orderCode);
        order.setUserId(user);
        order.setRecipientName(request.getRecipientName().trim());
        order.setRecipientPhone(request.getRecipientPhone().trim());
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setNote(request.getNote() != null ? request.getNote().trim() : null);
        order.setTotalAmount(totalAmount);
        order.setShippingFee(shippingFee);
        order.setFinalAmount(finalAmount);
        order.setStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod().trim());
        order.setPaymentStatus("UNPAID");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        order = orderRepository.save(order);

        List<OrderItems> orderItemsList = new ArrayList<>();
        for (CartItems item : cart.getCartItemsCollection()) {
            Books book = item.getBookId();
            BigDecimal unitPrice = book.getDiscountPrice() != null ? book.getDiscountPrice() : book.getPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItems orderItem = new OrderItems();
            orderItem.setOrderId(order);
            orderItem.setBookId(book);
            orderItem.setBookTitle(book.getTitle());
            orderItem.setPrice(unitPrice);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(itemSubtotal);

            orderRepository.saveItem(orderItem);
            orderItemsList.add(orderItem);

            // Deduct inventory stock
            book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
            book.setUpdatedAt(now);
            bookRepository.update(book);
        }

        order.setOrderItemsCollection(orderItemsList);

        // Clear user cart
        cartRepository.clearCart(cart.getId());

        return ApiResponse.success("Order placed successfully", OrderResponse.fromEntity(order));
    }

    @Override
    public ApiResponse<List<OrderResponse>> getUserOrders(Integer userId) {
        if (userId == null) {
            return ApiResponse.failed("Invalid user ID");
        }

        List<Orders> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Fetched user orders successfully", responses);
    }

    @Override
    public ApiResponse<OrderResponse> getOrderById(Integer userId, Integer orderId, boolean isAdmin) {
        if (orderId == null) {
            return ApiResponse.failed("Invalid order ID");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            return ApiResponse.failed("Order not found");
        }

        if (!isAdmin && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            return ApiResponse.failed("Access denied: You do not own this order");
        }

        return ApiResponse.success("Order details retrieved", OrderResponse.fromEntity(order));
    }

    @Override
    public ApiResponse<OrderResponse> getOrderByCode(Integer userId, String orderCode, boolean isAdmin) {
        if (orderCode == null || orderCode.trim().isEmpty()) {
            return ApiResponse.failed("Invalid order code");
        }

        Orders order = orderRepository.findByOrderCode(orderCode.trim());
        if (order == null) {
            return ApiResponse.failed("Order not found");
        }

        if (!isAdmin && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            return ApiResponse.failed("Access denied: You do not own this order");
        }

        return ApiResponse.success("Order details retrieved", OrderResponse.fromEntity(order));
    }

    @Override
    public ApiResponse<OrderResponse> cancelOrder(Integer userId, Integer orderId, boolean isAdmin) {
        if (orderId == null) {
            return ApiResponse.failed("Invalid order ID");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            return ApiResponse.failed("Order not found");
        }

        if (!isAdmin && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            return ApiResponse.failed("Access denied: You do not own this order");
        }

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            return ApiResponse.failed("Order is already cancelled");
        }

        if (!isAdmin && !"PENDING".equalsIgnoreCase(order.getStatus()) && !"PROCESSING".equalsIgnoreCase(order.getStatus())) {
            return ApiResponse.failed("Order cannot be cancelled in status: " + order.getStatus());
        }

        Date now = new Date();
        order.setStatus("CANCELLED");
        order.setUpdatedAt(now);

        // Restock inventory quantity
        if (order.getOrderItemsCollection() != null) {
            for (OrderItems item : order.getOrderItemsCollection()) {
                Books book = item.getBookId();
                if (book != null) {
                    book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
                    book.setUpdatedAt(now);
                    bookRepository.update(book);
                }
            }
        }

        Orders updatedOrder = orderRepository.update(order);
        return ApiResponse.success("Order cancelled successfully", OrderResponse.fromEntity(updatedOrder));
    }

    @Override
    public ApiResponse<List<OrderResponse>> getAllOrders(String status) {
        List<Orders> orders;
        if (status != null && !status.trim().isEmpty()) {
            orders = orderRepository.findByStatus(status.trim());
        } else {
            orders = orderRepository.findAll();
        }

        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Fetched all orders successfully", responses);
    }

    @Override
    public ApiResponse<OrderResponse> updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request) {
        if (orderId == null) {
            return ApiResponse.failed("Invalid order ID");
        }

        if (request == null) {
            return ApiResponse.failed("Update request cannot be null");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            return ApiResponse.failed("Order not found");
        }

        Date now = new Date();
        String newStatus = request.getStatus() != null ? request.getStatus().trim() : null;

        // If changing status to CANCELLED from a non-cancelled status, restore stock
        if (newStatus != null && "CANCELLED".equalsIgnoreCase(newStatus) && !"CANCELLED".equalsIgnoreCase(order.getStatus())) {
            if (order.getOrderItemsCollection() != null) {
                for (OrderItems item : order.getOrderItemsCollection()) {
                    Books book = item.getBookId();
                    if (book != null) {
                        book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
                        book.setUpdatedAt(now);
                        bookRepository.update(book);
                    }
                }
            }
        }

        if (newStatus != null && !newStatus.isEmpty()) {
            order.setStatus(newStatus);
        }

        if (request.getPaymentStatus() != null && !request.getPaymentStatus().trim().isEmpty()) {
            order.setPaymentStatus(request.getPaymentStatus().trim());
        }

        order.setUpdatedAt(now);
        Orders updatedOrder = orderRepository.update(order);

        return ApiResponse.success("Order status updated successfully", OrderResponse.fromEntity(updatedOrder));
    }

    private String generateOrderCode() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
