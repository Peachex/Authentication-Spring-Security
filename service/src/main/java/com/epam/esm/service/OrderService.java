package com.epam.esm.service;

import com.epam.esm.dto.Order;

import java.util.List;

public interface OrderService<T extends Order> {
    List<T> findByUserId(int page, int elements, String id, String userName);

    long createOrder(String userId, String certificateId, String userEmailFromToken);

    T findByUserIdAndOrderId(String userId, String orderId, String userName);

    T findById(String id);

    List<T> findWithCurrentCertificate(String certificateId);
}
