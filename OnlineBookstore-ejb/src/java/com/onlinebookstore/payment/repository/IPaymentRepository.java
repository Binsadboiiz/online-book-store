package com.onlinebookstore.payment.repository;

import com.onlinebookstore.payment.entity.Payments;
import java.util.List;

public interface IPaymentRepository {
    List<Payments> findAll();

    Payments findById(Integer id);

    Payments findByTransactionCode(String transactionCode);

    List<Payments> findByOrderId(Integer orderId);

    List<Payments> findByUserId(Integer userId);

    List<Payments> findByStatus(String status);

    Payments save(Payments payment);

    Payments update(Payments payment);

    boolean delete(Integer id);
}

