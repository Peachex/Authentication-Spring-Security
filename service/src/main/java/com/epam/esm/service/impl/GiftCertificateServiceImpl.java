package com.epam.esm.service.impl;

import com.epam.esm.constant.entity.GiftCertificateFieldName;
import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.creator.criteria.Criteria;
import com.epam.esm.dao.creator.criteria.search.FullMatchSearchCertificateCriteria;
import com.epam.esm.dao.creator.criteria.search.PartMatchSearchCertificateCriteria;
import com.epam.esm.dao.creator.criteria.sort.FieldSortCertificateCriteria;
import com.epam.esm.dto.GiftCertificate;
import com.epam.esm.dto.Order;
import com.epam.esm.dto.Tag;
import com.epam.esm.exception.DeleteCertificateInUseException;
import com.epam.esm.exception.InvalidFieldException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.TagValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService<GiftCertificate> {
    private static final String ASC_SORT_ORDERING = "ASC";
    private static final String DESC_SORT_ORDERING = "DESC";
    private final GiftCertificateDao<GiftCertificate> dao;
    private final TagService<Tag> tagService;
    private final GiftCertificateValidator certificateValidator;
    private final TagValidator tagValidator;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao<GiftCertificate> dao, TagService<Tag> tagService,
                                      GiftCertificateValidator certificateValidator, TagValidator tagValidator) {
        this.dao = dao;
        this.tagService = tagService;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
    }

    @Override
    public long insert(GiftCertificate giftCertificate) {
        long id;
        if (certificateValidator.isGiftCertificateCreationFormValid(giftCertificate)) {
            LocalDateTime currentTime = LocalDateTime.now();
            giftCertificate.setCreateDate(currentTime);
            giftCertificate.setLastUpdateDate(currentTime);
            giftCertificate.setAvailable(true);

            if (!CollectionUtils.isEmpty(giftCertificate.getTags())) {
                Set<Tag> allTags = new HashSet<>(tagService.findAll());
                Set<Tag> existingTags = SetUtils.intersection(allTags, giftCertificate.getTags());
                if (!existingTags.isEmpty()) {
                    existingTags.stream().filter(t -> !t.isAvailable()).peek(t -> t.setAvailable(true))
                            .forEach(t -> tagService.updateAvailability(String.valueOf(t.getId()), true));
                }
                Set<Tag> newTags = new HashSet<>(CollectionUtils.removeAll(giftCertificate.getTags(), existingTags));
                newTags.forEach(t -> t.setAvailable(true));
                giftCertificate.setTags(newTags);

                id = dao.insert(giftCertificate);
                GiftCertificate certificateWithAllTags = dao.findById(id).get();
                certificateWithAllTags.setTags(SetUtils.union(newTags, existingTags));
                dao.update(certificateWithAllTags);
            } else {
                id = dao.insert(giftCertificate);
            }
        } else {
            throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE, ErrorName.INVALID_GIFT_CERTIFICATE,
                    giftCertificate.toString());
        }
        return id;
    }

    @Override
    public void delete(String id, List<Order> orders) {
        try {
            GiftCertificate certificate = dao.findById(Long.parseLong(id)).orElseThrow(() ->
                    new ResourceNotFoundException(ErrorCode.GIFT_CERTIFICATE, ErrorName.RESOURCE_NOT_FOUND, id));
            if (CollectionUtils.isEmpty(orders)) {
                if (!CollectionUtils.isEmpty(certificate.getTags())) {
                    dao.disconnectAllTags(certificate);
                }
                dao.delete(certificate.getId());
            } else {
                if (LocalDateTime.now().isAfter(certificate.getCreateDate().plusDays(certificate.getDuration()))) {
                    certificate.setAvailable(false);
                    dao.update(certificate);
                } else {
                    throw new DeleteCertificateInUseException(ErrorCode.ORDER, ErrorName.CERTIFICATE_IN_USE, id);
                }
            }
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE, ErrorName.INVALID_GIFT_CERTIFICATE_ID, id);
        }
    }

    @Override
    public boolean update(String id, GiftCertificate newCertificate) {
        try {
            GiftCertificate oldCertificate = dao.findById(Long.parseLong(id)).orElseThrow(() ->
                    new ResourceNotFoundException(ErrorCode.GIFT_CERTIFICATE,
                            ErrorName.RESOURCE_NOT_FOUND, id));

            if (updateCertificateFields(oldCertificate, newCertificate)) {
                oldCertificate.setLastUpdateDate(LocalDateTime.now());
                if (!CollectionUtils.isEmpty(oldCertificate.getTags())) {
                    List<Tag> existingTags = (List<Tag>) CollectionUtils.intersection(tagService.findAll(),
                            oldCertificate.getTags());

                    existingTags.stream().filter(t -> !t.isAvailable()).peek(t -> t.setAvailable(true))
                            .forEach(t -> tagService.updateAvailability(String.valueOf(t.getId()), true));

                    List<Tag> newTags = (List<Tag>) CollectionUtils.removeAll(oldCertificate.getTags(), existingTags);
                    newTags.forEach(tagService::insert);

                    Set<Tag> allCertificateTags = new HashSet<>(existingTags);
                    allCertificateTags.addAll(newTags);
                    oldCertificate.setTags(allCertificateTags);
                }
                dao.update(oldCertificate);
            } else {
                throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE, ErrorName.INVALID_GIFT_CERTIFICATE,
                        newCertificate.toString());
            }
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE, ErrorName.INVALID_GIFT_CERTIFICATE_ID, id);
        }
        return true;
    }

    @Override
    public GiftCertificate findById(String id) {
        try {
            Optional<GiftCertificate> certificateOptional = dao.findById(Long.parseLong(id));
            if (!certificateOptional.isPresent() || !certificateOptional.get().isAvailable()) {
                throw new ResourceNotFoundException(ErrorCode.GIFT_CERTIFICATE, ErrorName.RESOURCE_NOT_FOUND, id);
            }
            return certificateOptional.get();
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE,
                    ErrorName.INVALID_GIFT_CERTIFICATE_ID, id);
        }
    }

    @Override
    public List<GiftCertificate> findAll(int page, int elements) {
        if (page < 1 || elements < 1) {
            throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE, ErrorName.INVALID_PAGINATION_DATA,
                    page + ", " + elements);
        }
        return dao.findAll(page, elements);
    }

    @Override
    public List<GiftCertificate> findCertificatesWithTagsByCriteria(boolean isPaginationActive, int page, int elements,
                                                                    List<String> tagsNames, String certificateName,
                                                                    String certificateDescription, String sortByName,
                                                                    String sortByDate) {
        if (isPaginationActive && (page < 1 || elements < 1)) {
            throw new InvalidFieldException(ErrorCode.GIFT_CERTIFICATE, ErrorName.INVALID_PAGINATION_DATA,
                    page + ", " + elements);
        }
        List<Criteria<GiftCertificate>> certificateCriteriaList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tagsNames) && tagsNames.stream().allMatch(tagValidator::isNameValid)) {
            List<Tag> tags = new ArrayList<>();
            tagsNames.forEach(t -> tags.add(tagService.findByName(t)));
            certificateCriteriaList.add(new FullMatchSearchCertificateCriteria(tags));
        }
        if (certificateValidator.isNameValid(certificateName)) {
            certificateCriteriaList.add(new PartMatchSearchCertificateCriteria(GiftCertificateFieldName.NAME,
                    certificateName));
        }
        if (certificateValidator.isDescriptionValid(certificateDescription)) {
            certificateCriteriaList.add(new PartMatchSearchCertificateCriteria(GiftCertificateFieldName.DESCRIPTION,
                    certificateDescription));
        }
        if (sortByName != null && !sortByName.isEmpty()) {
            String sortOrdering = sortByName.equalsIgnoreCase(ASC_SORT_ORDERING) ? ASC_SORT_ORDERING
                    : DESC_SORT_ORDERING;
            certificateCriteriaList.add(new FieldSortCertificateCriteria(GiftCertificateFieldName.NAME, sortOrdering));
        }
        if (sortByDate != null && !sortByDate.isEmpty()) {
            String sortOrdering = sortByDate.equalsIgnoreCase(ASC_SORT_ORDERING) ? ASC_SORT_ORDERING
                    : DESC_SORT_ORDERING;
            certificateCriteriaList.add(new FieldSortCertificateCriteria(GiftCertificateFieldName.CREATE_DATE,
                    sortOrdering));
        }
        return dao.findWithTags(isPaginationActive, page, elements, certificateCriteriaList);
    }

    private boolean updateCertificateFields(GiftCertificate oldCertificate, GiftCertificate newCertificate) {
        boolean result = false;
        if (certificateValidator.isNameValid(newCertificate.getName())) {
            oldCertificate.setName(newCertificate.getName());
            result = true;
        }
        if (certificateValidator.isDescriptionValid(newCertificate.getDescription())) {
            oldCertificate.setDescription(newCertificate.getDescription());
            result = true;
        }
        if (certificateValidator.isPriceValid(newCertificate.getPrice())) {
            oldCertificate.setPrice(newCertificate.getPrice());
            result = true;
        }
        if (certificateValidator.isDurationValid(newCertificate.getDuration())) {
            oldCertificate.setDuration(newCertificate.getDuration());
            result = true;
        }
        if (certificateValidator.areGiftCertificateTagsValid(newCertificate.getTags())) {
            oldCertificate.setTags(SetUtils.union(oldCertificate.getTags(), newCertificate.getTags()));
            result = true;
        }
        return result;
    }
}
