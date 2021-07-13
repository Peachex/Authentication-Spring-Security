package com.epam.esm.service.impl;

import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.dao.UserDao;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dto.SecurityUser;
import com.epam.esm.dto.User;
import com.epam.esm.exception.InvalidFieldException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService<User> {
    private final UserDao<User> dao;

    @Autowired
    public UserServiceImpl(UserDao<User> dao) {
        this.dao = dao;
    }

    @Override
    public User findById(String id) {
        try {
            return dao.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.USER, ErrorName.RESOURCE_NOT_FOUND, id));
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.USER, ErrorName.INVALID_USER_ID, id);
        }
    }

    @Override
    public User findByEmail(String email) {
        return dao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER,
                ErrorName.RESOURCE_NOT_FOUND, email));
    }

    @Override
    public List<User> findAll(int page, int elements) {
        if (page < 1 || elements < 1) {
            throw new InvalidFieldException(ErrorCode.USER, ErrorName.INVALID_PAGINATION_DATA, page + ", " + elements);
        }
        return dao.findAll(page, elements);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = dao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.USER, ErrorName.RESOURCE_NOT_FOUND, email));
        return SecurityUser.fromUser(user);
    }
}
