package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificate;
import com.epam.esm.dto.Order;

import java.util.List;

public interface GiftCertificateService<T extends GiftCertificate> {
    long insert(T t);

    void delete(String id, List<Order> orders);

    boolean update(String id, T giftCertificate);

    T findById(String id);

    List<GiftCertificate> findAll(int page, int elements);

    List<GiftCertificate> findCertificatesWithTagsByCriteria(boolean isPaginationActive, int page, int elements,
                                                             List<String> tagsNames, String certificateName,
                                                             String certificateDescription, String sortByName,
                                                             String sortByDate);
}
