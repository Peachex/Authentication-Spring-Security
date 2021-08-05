package com.epam.esm.dao;

import com.epam.esm.dto.User;

import java.util.List;
import java.util.Optional;

public interface UserDao<T extends User> {
    Optional<T> findById(long id);

    Optional<T> findByEmail(String email);

    List<T> findAll(int page, int elements);

    long insert(T t);
}
