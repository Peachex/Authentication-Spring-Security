package com.epam.esm.service.impl;

import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dto.Tag;
import com.epam.esm.dto.User;
import com.epam.esm.exception.InvalidFieldException;
import com.epam.esm.exception.ResourceDuplicateException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;
import com.epam.esm.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService<Tag> {
    private final TagDao<Tag> dao;
    private final UserService<User> userService;
    private final TagValidator validator;

    @Autowired
    public TagServiceImpl(TagDao<Tag> dao, UserService<User> userService, TagValidator validator) {
        this.dao = dao;
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public long insert(Tag tag) {
        if (!validator.isNameValid(tag.getName())) {
            throw new InvalidFieldException(ErrorCode.TAG, ErrorName.INVALID_TAG_ID, tag.getName());
        }
        Optional<Tag> tagOptional = dao.findByName(tag.getName());
        if (tagOptional.isPresent()) {
            Tag tagWithId = tagOptional.get();
            if (!tagWithId.isAvailable()) {
                tagWithId.setAvailable(true);
                dao.update(tagWithId);
                return tagWithId.getId();
            } else {
                throw new ResourceDuplicateException(ErrorCode.TAG, ErrorName.TAG_DUPLICATE, tag.getName());
            }
        }
        tag.setAvailable(true);
        return dao.insert(tag);
    }

    @Override
    public Tag findById(String id) {
        try {
            Optional<Tag> tagOptional = dao.findById(Long.parseLong(id));
            if (!tagOptional.isPresent() || !tagOptional.get().isAvailable()) {
                throw new ResourceNotFoundException(ErrorCode.TAG, ErrorName.RESOURCE_NOT_FOUND, id);
            }
            return tagOptional.get();
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.TAG, ErrorName.INVALID_TAG_ID, id);
        }
    }

    @Override
    public Tag findByName(String name) {
        if (validator.isNameValid(name)) {
            Optional<Tag> tagOptional = dao.findByName(name);
            if (!tagOptional.isPresent() || !tagOptional.get().isAvailable()) {
                throw new ResourceNotFoundException(ErrorCode.TAG, ErrorName.RESOURCE_NOT_FOUND, name);
            }
            return tagOptional.get();
        } else {
            throw new InvalidFieldException(ErrorCode.TAG, ErrorName.INVALID_TAG_NAME, name);
        }
    }

    @Override
    public Tag findMostUsedTagOfUserWithHighestCostOfAllOrders(String userId) {
        return dao.findMostUsedTagOfUserWithHighestCostOfAllOrders(userService.findById(userId).getId()).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.TAG, ErrorName.RESOURCE_NOT_FOUND, userId));
    }

    @Override
    public List<Tag> findAll(int page, int elements) {
        if (page < 1 || elements < 1) {
            throw new InvalidFieldException(ErrorCode.TAG, ErrorName.INVALID_PAGINATION_DATA, page + ", " + elements);
        }
        return dao.findAll(page, elements);
    }

    @Override
    public List<Tag> findAll() {
        return dao.findAll();
    }

    @Override
    public void delete(String id, boolean isTagInUse) {
        try {
            if (isTagInUse) {
                updateAvailability(id, false);
            } else {
                if (!dao.delete(Long.parseLong(id))) {
                    throw new ResourceNotFoundException(ErrorCode.TAG, ErrorName.RESOURCE_NOT_FOUND, id);
                }
            }
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.TAG, ErrorName.INVALID_TAG_ID, id);
        }
    }

    @Override
    public void updateAvailability(String id, boolean isAvailable) {
        try {
            Tag tag = dao.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TAG,
                    ErrorName.RESOURCE_NOT_FOUND, id));
            tag.setAvailable(isAvailable);
            dao.update(tag);
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.TAG, ErrorName.INVALID_TAG_ID, id);
        }
    }
}
