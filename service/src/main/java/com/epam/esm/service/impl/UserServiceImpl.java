package com.epam.esm.service.impl;

import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.dao.UserDao;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dto.User;
import com.epam.esm.dto.UserRole;
import com.epam.esm.exception.InvalidFieldException;
import com.epam.esm.exception.ResourceDuplicateException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import com.epam.esm.util.UserDetailsConverter;
import com.epam.esm.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService<User> {
    private final UserDao<User> dao;
    private final UserValidator validator;
    private final PasswordEncoder encryptor;
    private final UserDetailsConverter userDetailsConverter;

    @Autowired
    public UserServiceImpl(UserDao<User> dao, UserValidator validator, PasswordEncoder encryptor,
                           UserDetailsConverter userDetailsConverter) {
        this.dao = dao;
        this.validator = validator;
        this.encryptor = encryptor;
        this.userDetailsConverter = userDetailsConverter;
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
    public long insert(User user) {
        if (!validator.isUserValid(user)) {
            throw new InvalidFieldException(ErrorCode.USER, ErrorName.INVALID_USER, user.toString());
        }
        if (dao.findByEmail(user.getEmail()).isPresent()) {
            throw new ResourceDuplicateException(ErrorCode.USER, ErrorName.USER_EMAIL_IN_USE, user.getEmail());
        }
        user.setRole(UserRole.USER);
        user.setActive(true);
        user.setPassword(encryptor.encode(user.getPassword()));
        return dao.insert(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = dao.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.USER, ErrorName.RESOURCE_NOT_FOUND, email));
        return userDetailsConverter.convert(user);
    }
}
