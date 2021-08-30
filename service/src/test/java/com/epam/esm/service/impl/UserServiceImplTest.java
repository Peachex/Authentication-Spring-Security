package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.dto.User;
import com.epam.esm.dto.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyInt;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserDao<User> dao;

    @BeforeAll
    public void init() {
        initMocks(this);
    }

    @Test
    public void findById() {
        User expected = new User(1, "Alice", "Green", "alice@gmail.com", "password", UserRole.ADMIN, true);
        when(dao.findById(anyLong())).thenReturn(Optional.of(expected));
        when(dao.findByEmail(anyString())).thenReturn(Optional.of(expected));
        User actual = service.findById("11", "alice@gmail.com");
        assertEquals(expected, actual);
    }

    @Test
    public void findAllTest() {
        List<User> expected = new ArrayList<>();
        when(dao.findAll(anyInt(), anyInt())).thenReturn(expected);
        List<User> actual = service.findAll(1, 5);
        assertEquals(expected, actual);
    }
}
