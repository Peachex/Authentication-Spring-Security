package com.epam.esm.dao.impl;

import com.epam.esm.constant.entity.GiftCertificateFieldName;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.creator.QueryCreator;
import com.epam.esm.dao.creator.criteria.Criteria;
import com.epam.esm.dto.GiftCertificate;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao<GiftCertificate> {
    private final QueryCreator<GiftCertificate> criteriaCreator;
    private EntityManager manager;

    @Autowired
    public GiftCertificateDaoImpl(QueryCreator<GiftCertificate> criteriaCreator) {
        this.criteriaCreator = criteriaCreator;
    }

    @PersistenceContext
    public void setManager(EntityManager manager) {
        this.manager = manager;
    }

    @Transactional
    @Override
    public long insert(GiftCertificate giftCertificate) {
        manager.persist(giftCertificate);
        return giftCertificate.getId();
    }

    @Transactional
    @Override
    public boolean delete(long id) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaDelete<GiftCertificate> criteria = builder.createCriteriaDelete(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.where(builder.equal(root.get(GiftCertificateFieldName.ID), id));
        return manager.createQuery(criteria).executeUpdate() == 1;
    }

    @Transactional
    @Override
    public boolean disconnectAllTags(GiftCertificate giftCertificate) {
        giftCertificate.setTags(null);
        manager.merge(giftCertificate);
        return CollectionUtils.isEmpty(giftCertificate.getTags());
    }

    @Transactional
    @Override
    public void update(GiftCertificate giftCertificate) {
        manager.clear();
        manager.merge(giftCertificate);
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        return Optional.ofNullable(manager.find(GiftCertificate.class, id));
    }

    @Override
    public List<GiftCertificate> findAll(int page, int elements) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteria.select(root);
        return (manager.createQuery(criteria)
                .setMaxResults(elements)
                .setFirstResult(elements * (page - 1))
                .getResultList());
    }

    @Override
    public List<GiftCertificate> findWithTags(boolean isPaginationActive, int page, int elements,
                                              List<Criteria<GiftCertificate>> certificateCriteriaList) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteria = builder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteria.from(GiftCertificate.class);
        criteriaCreator.createCriteria(certificateCriteriaList, criteria, builder, root);
        return (isPaginationActive ? manager.createQuery(criteria).setMaxResults(elements)
                .setFirstResult(elements * (page - 1)).getResultList() : manager.createQuery(criteria).getResultList());
    }
}
