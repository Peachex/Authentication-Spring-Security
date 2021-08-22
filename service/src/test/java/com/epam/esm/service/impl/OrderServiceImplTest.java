package com.epam.esm.service.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dto.GiftCertificate;
import com.epam.esm.dto.Order;
import com.epam.esm.dto.Tag;
import com.epam.esm.dto.User;
import com.epam.esm.dto.UserRole;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceImplTest {
    @InjectMocks
    public OrderServiceImpl orderService;

    @Mock
    private OrderDao<Order> dao;

    @Mock
    private UserService<User> userService;

    @Mock
    private GiftCertificateService<GiftCertificate> certificateService;

    @BeforeAll
    public void init() {
        initMocks(this);
    }

    @Test
    public void createOrderTest() {
        long expected = 11;
        when(dao.insert(any())).thenReturn(expected);

        when(userService.findById(anyString(), anyString()))
                .thenReturn(new User(1, "Alice", "Green", "alice@gmail.com", "password", UserRole.ADMIN, true));

        when(certificateService.findById(anyString()))
                .thenReturn(new GiftCertificate(2, true, "Sand", "Yellow sand", new BigDecimal("2"),
                        24, LocalDateTime.of(2020, 5, 5, 23, 42, 12,
                        112000000), null, new HashSet<>()));

        long actual = orderService.createOrder("1", "1", "alice@gmail.com");
        assertEquals(expected, actual);
    }

    @Test
    public void findByIdTest() {
        Order expected = new Order();
        expected.setPrice(BigDecimal.TEN);
        expected.setUser(new User(1, "Alice", "Green", "alice@gmail.com", "password", UserRole.ADMIN, true));
        expected.setTimestamp(LocalDateTime.now());

        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag(3, true, "#warm"));

        expected.setGiftCertificate(new GiftCertificate(5, true, "Ferry", "Ferryman", BigDecimal.valueOf(0.99), 14,
                LocalDateTime.of(2019, 11, 19, 11, 10, 11, 111000000), null, tags));

        when(dao.findById(anyLong())).thenReturn(Optional.of(expected));
        Order actual = orderService.findById("11");
        assertEquals(expected, actual);
    }
}
