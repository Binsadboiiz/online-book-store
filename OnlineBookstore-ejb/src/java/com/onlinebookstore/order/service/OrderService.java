package com.onlinebookstore.order.service;

import com.onlinebookstore.book.entity.Books;
import com.onlinebookstore.book.repository.IBookRepository;
import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;
import com.onlinebookstore.cart.repository.ICartRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.order.dto.CreateOrderRequest;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.dto.UpdateOrderStatusRequest;
import com.onlinebookstore.order.entity.OrderItems;
import com.onlinebookstore.order.entity.Orders;
import com.onlinebookstore.order.enums.OrderStatus;
import com.onlinebookstore.order.repository.IOrderRepository;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ApiResponse<OrderResponse> createOrder(Integer userId, CreateOrderRequest request) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null) {
            throw new BadRequestException("Order request data cannot be null");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getCartItemsCollection() == null || cart.getCartItemsCollection().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Validate stock availability for all cart items
        for (CartItems item : cart.getCartItemsCollection()) {
            Books book = item.getBookId();
            if (book == null || !Boolean.TRUE.equals(book.getIsActive())) {
                throw new BadRequestException("Book '" + (book != null ? book.getTitle() : "Unknown") + "' is inactive or no longer available");
            }
            if (item.getQuantity() > book.getStockQuantity()) {
                throw new BadRequestException("Quantity for '" + book.getTitle() + "' exceeds available stock (" + book.getStockQuantity() + ")");
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
        order.setStatus(OrderStatus.PENDING.name());
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

            // Deduct inventory stock atomically at database level within this transaction
            int updatedRows = bookRepository.deductStock(book.getId(), item.getQuantity());
            if (updatedRows == 0) {
                // Throwing exception marks EJB transaction for automatic ROLLBACK
                throw new BadRequestException("Failed to place order: Book '" + book.getTitle() + "' is out of stock or inactive");
            }
        }

        order.setOrderItemsCollection(orderItemsList);

        // Clear user cart in DB and in-memory collection within transaction
        if (cart.getCartItemsCollection() != null) {
            cart.getCartItemsCollection().clear();
        }
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
    public ApiResponse<OrderResponse> getOrderById(Integer userId, Integer orderId, boolean isManager) {
        if (orderId == null) {
            return ApiResponse.failed("Invalid order ID");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            return ApiResponse.failed("Order not found");
        }

        if (!isManager && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            return ApiResponse.failed("Access denied: You can only view your own orders");
        }

        return ApiResponse.success("Order details retrieved", OrderResponse.fromEntity(order));
    }

    @Override
    public ApiResponse<OrderResponse> getOrderByCode(Integer userId, String orderCode, boolean isManager) {
        if (orderCode == null || orderCode.trim().isEmpty()) {
            return ApiResponse.failed("Invalid order code");
        }

        Orders order = orderRepository.findByOrderCode(orderCode.trim());
        if (order == null) {
            return ApiResponse.failed("Order not found");
        }

        if (!isManager && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            return ApiResponse.failed("Access denied: You can only view your own orders");
        }

        return ApiResponse.success("Order details retrieved", OrderResponse.fromEntity(order));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ApiResponse<OrderResponse> cancelOrder(Integer userId, Integer orderId, boolean isManager) {
        if (orderId == null) {
            throw new BadRequestException("Invalid order ID");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (!isManager && (order.getUserId() == null || !order.getUserId().getId().equals(userId))) {
            throw new BadRequestException("Access denied: You can only cancel your own orders");
        }

        OrderStatus currentStatus = OrderStatus.fromString(order.getStatus());
        if (currentStatus != null && currentStatus.isTerminalState()) {
            throw new BadRequestException("Completed, delivered, or cancelled orders cannot be modified or cancelled");
        }

        if (currentStatus != null && !currentStatus.isValidTransitionTo(OrderStatus.CANCELLED)) {
            throw new BadRequestException("Order cannot be cancelled from status: " + order.getStatus());
        }

        Date now = new Date();
        order.setStatus(OrderStatus.CANCELLED.name());
        order.setUpdatedAt(now);

        // Restock inventory quantity atomically at database level
        if (order.getOrderItemsCollection() != null) {
            for (OrderItems item : order.getOrderItemsCollection()) {
                Books book = item.getBookId();
                if (book != null) {
                    bookRepository.restock(book.getId(), item.getQuantity());
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ApiResponse<OrderResponse> updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request) {
        if (orderId == null) {
            throw new BadRequestException("Invalid order ID");
        }

        if (request == null) {
            throw new BadRequestException("Update request cannot be null");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        OrderStatus currentStatus = OrderStatus.fromString(order.getStatus());
        if (currentStatus != null && currentStatus.isTerminalState()) {
            throw new BadRequestException("Order with status '" + order.getStatus() + "' is completed, delivered, or cancelled and cannot be modified");
        }

        Date now = new Date();
        String newStatusStr = request.getStatus() != null ? request.getStatus().trim() : null;

        if (newStatusStr != null && !newStatusStr.isEmpty()) {
            OrderStatus targetStatus = OrderStatus.fromString(newStatusStr);
            if (targetStatus == null) {
                throw new BadRequestException("Invalid target order status: " + newStatusStr);
            }

            if (currentStatus != null && !currentStatus.isValidTransitionTo(targetStatus)) {
                throw new BadRequestException("Invalid status transition from '" + currentStatus.name() + "' to '" + targetStatus.name() + "'");
            }

            // Restore stock atomically if transitioning to CANCELLED
            if (targetStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
                if (order.getOrderItemsCollection() != null) {
                    for (OrderItems item : order.getOrderItemsCollection()) {
                        Books book = item.getBookId();
                        if (book != null) {
                            bookRepository.restock(book.getId(), item.getQuantity());
                        }
                    }
                }
            }

            order.setStatus(targetStatus.name());
        }

        if (request.getPaymentStatus() != null && !request.getPaymentStatus().trim().isEmpty()) {
            order.setPaymentStatus(request.getPaymentStatus().trim());
        }

        order.setUpdatedAt(now);
        Orders updatedOrder = orderRepository.update(order);

        return ApiResponse.success("Order status updated successfully", OrderResponse.fromEntity(updatedOrder));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ApiResponse<String> deleteOrder(Integer orderId, boolean isManager) {
        if (!isManager) {
            throw new BadRequestException("Access denied: Only managers can delete orders");
        }

        if (orderId == null) {
            throw new BadRequestException("Invalid order ID");
        }

        Orders order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        OrderStatus currentStatus = OrderStatus.fromString(order.getStatus());
        if (currentStatus != null && currentStatus.isTerminalState()) {
            throw new BadRequestException("Completed, delivered, or cancelled orders cannot be deleted");
        }

        // Restore stock atomically for active uncompleted order before deletion
        if (order.getOrderItemsCollection() != null) {
            for (OrderItems item : order.getOrderItemsCollection()) {
                Books book = item.getBookId();
                if (book != null) {
                    bookRepository.restock(book.getId(), item.getQuantity());
                }
            }
        }

        boolean deleted = orderRepository.delete(orderId);
        if (!deleted) {
            throw new BadRequestException("Failed to delete order");
        }

        return ApiResponse.success("Order deleted successfully", null);
    }

    private String generateOrderCode() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
