package com.epam.esm.dao.impl;

import com.epam.esm.config.EntityManagerFactoryConfiguration;
import com.epam.esm.constant.entity.TagFieldName;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.creator.GiftCertificateQueryCreator;
import com.epam.esm.dao.creator.criteria.Criteria;
import com.epam.esm.dao.creator.criteria.search.FullMatchSearchCertificateCriteria;
import com.epam.esm.dto.GiftCertificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ContextConfiguration(classes = {GiftCertificateDaoImpl.class, GiftCertificateQueryCreator.class,
        EntityManagerFactoryConfiguration.class}, loader = AnnotationConfigContextLoader.class)
@SpringBootTest
public class GiftCertificateDaoImplTest {
    private GiftCertificate certificate;
    @Autowired
    private GiftCertificateDao<GiftCertificate> dao;

    @BeforeEach
    public void init() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("New Test Certificate");
        certificate.setDescription("Description (test)");
        certificate.setPrice(BigDecimal.ONE);
        certificate.setCreateDate(LocalDateTime.now());
        certificate.setDuration(11);
        certificate.setLastUpdateDate(null);
        certificate.setTags(null);
        certificate.setAvailable(true);
        this.certificate = certificate;
    }

    @Test
    public void findWithTagsTest() {
        List<GiftCertificate> expected = new ArrayList<>();
        List<Criteria<GiftCertificate>> criteriaList = new ArrayList<>();
        criteriaList.add(new FullMatchSearchCertificateCriteria(TagFieldName.NAME, "#longverylongtagname"));
        List<GiftCertificate> actual = dao.findWithTags(false, 0, 0, criteriaList);
        assertEquals(expected, actual);
    }

    @Test
    public void insertTest() {
        long expected = 6;
        long actual = dao.insert(certificate);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteTest() {
        boolean actual = dao.delete(5);
        assertTrue(actual);
    }

    @Test
    public void findByIdTest() {
        Optional<GiftCertificate> expected = Optional.empty();
        Optional<GiftCertificate> actual = dao.findById(12345);
        assertEquals(expected, actual);
    }
}
