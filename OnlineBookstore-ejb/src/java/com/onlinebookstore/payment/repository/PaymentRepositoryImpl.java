package com.onlinebookstore.payment.repository;

import com.onlinebookstore.payment.entity.Payments;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PaymentRepositoryImpl implements IPaymentRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public List<Payments> findAll() {
        return entityManager.createQuery("SELECT p FROM Payments p ORDER BY p.createdAt DESC", Payments.class)
                .getResultList();
    }

    @Override
    public Payments findById(Integer id) {
        return entityManager.find(Payments.class, id);
    }

    @Override
    public Payments findByTransactionCode(String transactionCode) {
        try {
            return entityManager.createQuery("SELECT p FROM Payments p WHERE p.transactionCode = :transactionCode", Payments.class)
                    .setParameter("transactionCode", transactionCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Payments> findByOrderId(Integer orderId) {
        return entityManager.createQuery("SELECT p FROM Payments p WHERE p.orderId.id = :orderId ORDER BY p.createdAt DESC", Payments.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    @Override
    public List<Payments> findByUserId(Integer userId) {
        return entityManager.createQuery("SELECT p FROM Payments p WHERE p.orderId.userId.id = :userId ORDER BY p.createdAt DESC", Payments.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Payments> findByStatus(String status) {
        return entityManager.createQuery("SELECT p FROM Payments p WHERE p.status = :status ORDER BY p.createdAt DESC", Payments.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public Payments save(Payments payment) {
        entityManager.persist(payment);
        return payment;
    }

    @Override
    public Payments update(Payments payment) {
        return entityManager.merge(payment);
    }

    @Override
    public boolean delete(Integer id) {
        Payments payment = findById(id);
        if (payment != null) {
            entityManager.remove(payment);
            return true;
        }
        return false;
    }
}

