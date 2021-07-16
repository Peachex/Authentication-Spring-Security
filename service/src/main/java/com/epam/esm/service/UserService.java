package com.epam.esm.service;

import com.epam.esm.dto.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService<T extends User> extends UserDetailsService {
    T findById(String id);

    T findByEmail(String email);

    List<T> findAll(int page, int elements);

    long insert(T t);
}
