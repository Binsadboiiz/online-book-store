package com.onlinebookstore.order.repository;

import com.onlinebookstore.order.entity.Orders;
import com.onlinebookstore.order.entity.OrderItems;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OrderRepositoryImpl implements IOrderRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public Orders findById(Integer id) {
        return entityManager.find(Orders.class, id);
    }

    @Override
    public Orders findByOrderCode(String orderCode) {
        try {
            return entityManager.createQuery("SELECT o FROM Orders o WHERE o.orderCode = :orderCode", Orders.class)
                    .setParameter("orderCode", orderCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Orders> findByUserId(Integer userId) {
        return entityManager.createQuery("SELECT o FROM Orders o WHERE o.userId.id = :userId ORDER BY o.createdAt DESC", Orders.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Orders> findAll() {
        return entityManager.createQuery("SELECT o FROM Orders o ORDER BY o.createdAt DESC", Orders.class)
                .getResultList();
    }

    @Override
    public List<Orders> findByStatus(String status) {
        return entityManager.createQuery("SELECT o FROM Orders o WHERE o.status = :status ORDER BY o.createdAt DESC", Orders.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public Orders save(Orders order) {
        entityManager.persist(order);
        return order;
    }

    @Override
    public Orders update(Orders order) {
        return entityManager.merge(order);
    }

    @Override
    public boolean delete(Integer id) {
        Orders order = findById(id);
        if (order != null) {
            entityManager.remove(order);
            return true;
        }
        return false;
    }

    @Override
    public OrderItems findOrderItemById(Integer orderItemId) {
        return entityManager.find(OrderItems.class, orderItemId);
    }

    @Override
    public OrderItems saveItem(OrderItems item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    public OrderItems updateItem(OrderItems item) {
        return entityManager.merge(item);
    }

    @Override
    public boolean deleteItem(Integer orderItemId) {
        OrderItems item = findOrderItemById(orderItemId);
        if (item != null) {
            entityManager.remove(item);
            return true;
        }
        return false;
    }
}

