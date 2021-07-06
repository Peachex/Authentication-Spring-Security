package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dto.GiftCertificate;
import com.epam.esm.exception.InvalidFieldException;
import com.epam.esm.validator.GiftCertificateValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GiftCertificateServiceImplTest {
    private static GiftCertificate giftCertificate;

    @InjectMocks
    private GiftCertificateServiceImpl service;

    @Mock
    private GiftCertificateDao<GiftCertificate> dao;

    @Mock
    private GiftCertificateValidator certificateValidator;

    @BeforeAll
    public void init() {
        initMocks(this);
        service = new GiftCertificateServiceImpl(dao, null, certificateValidator, null);
        giftCertificate = new GiftCertificate(2, "Sand", "Yellow sand", new BigDecimal("2"), 24,
                LocalDateTime.of(2020, 5, 5, 23, 42, 12, 112000000),
                null, new HashSet<>());
    }

    @Test
    public void findAllTest() {
        List<GiftCertificate> expected = new ArrayList<>();
        expected.add(giftCertificate);
        when(dao.findAll(1,1)).thenReturn(expected);
        List<GiftCertificate> actual = service.findAll(1, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void findByIdTest() {
        GiftCertificate expected = giftCertificate;
        when(dao.findById(anyLong())).thenReturn(Optional.of(expected));
        GiftCertificate actual = service.findById("1");
        assertEquals(expected, actual);
    }

    @Test()
    public void insertTest() {
        when(certificateValidator.isGiftCertificateCreationFormValid(any(GiftCertificate.class))).thenReturn(false);
        assertThrows(InvalidFieldException.class, () -> service.insert(giftCertificate));
    }
}
