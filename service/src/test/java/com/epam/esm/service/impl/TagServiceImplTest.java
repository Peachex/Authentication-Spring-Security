package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dto.Tag;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TagServiceImplTest {
    @InjectMocks
    private TagServiceImpl service;

    @Mock
    private TagDao<Tag> dao;

    @Mock
    TagValidator validator;

    @BeforeAll
    public void init() {
        initMocks(this);
        service = new TagServiceImpl(dao, null, validator);
    }

    @Test
    public void insertTest() {
        long expected = 11;
        Tag tag = new Tag("#new");
        when(validator.isNameValid(anyString())).thenReturn(true);
        when(dao.insert(tag)).thenReturn(11L);
        when(dao.findByName(anyString())).thenReturn(Optional.empty());
        long actual = service.insert(tag);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteTest() {
        when(dao.delete(anyLong())).thenReturn(true);
        boolean actual = service.delete("11");
        assertTrue(actual);
    }

    @Test
    public void findByIdTest() {
        Tag expected = new Tag("#cool");
        when(dao.findById(anyLong())).thenReturn(Optional.of(expected));
        Tag actual = service.findById("11");
        assertEquals(expected, actual);
    }
}
