package com.onlinebookstore.order.repository;

import com.onlinebookstore.order.entity.Orders;
import com.onlinebookstore.order.entity.OrderItems;
import java.util.List;

public interface IOrderRepository {
    Orders findById(Integer id);

    Orders findByOrderCode(String orderCode);

    List<Orders> findByUserId(Integer userId);

    List<Orders> findAll();

    List<Orders> findByStatus(String status);

    Orders save(Orders order);

    Orders update(Orders order);

    boolean delete(Integer id);

    OrderItems findOrderItemById(Integer orderItemId);

    OrderItems saveItem(OrderItems item);

    OrderItems updateItem(OrderItems item);

    boolean deleteItem(Integer orderItemId);
}