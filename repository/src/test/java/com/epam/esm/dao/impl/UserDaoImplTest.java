package com.epam.esm.dao.impl;

import com.epam.esm.config.EntityManagerFactoryConfiguration;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dto.User;
import com.epam.esm.dto.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@ContextConfiguration(classes = {UserDaoImpl.class, EntityManagerFactoryConfiguration.class},
        loader = AnnotationConfigContextLoader.class)
@SpringBootTest
public class UserDaoImplTest {

    @Autowired
    private UserDao<User> dao;

    @Test
    public void findByIdTest() {
        Optional<User> expected = Optional.of(new User(1, "Alice", "Green", "alice@gmail.com",
                "$2y$12$XRUTT3VBVCzf64HS.fCcse0QhSiFZlfTb7E8XZ6iBzTjvPyhC8h.W", UserRole.ADMIN, true));
        Optional<User> actual = dao.findById(1);
        assertEquals(expected, actual);
    }

    @Test
    public void findAllTest() {
        List<User> expected = new ArrayList<>();
        List<User> actual = dao.findAll(12345, 12345);
        assertEquals(expected, actual);
    }
}
