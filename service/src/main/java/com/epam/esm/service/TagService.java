package com.epam.esm.service;

import com.epam.esm.dto.Tag;

import java.util.List;

public interface TagService<T extends Tag> {
    long insert(T t);

    T findById(String id);

    T findByName(String name);

    T findMostUsedTagOfUserWithHighestCostOfAllOrders(String userId);

    List<T> findAll(int page, int elements);

    List<T> findAll();

    void delete(String id, boolean isTagInUse);

    void updateAvailability(String id, boolean isAvailable);
}
