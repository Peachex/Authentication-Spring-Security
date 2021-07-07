package com.epam.esm.dao.impl;

import com.epam.esm.constant.entity.GiftCertificateFieldName;
import com.epam.esm.constant.entity.OrderFieldName;
import com.epam.esm.constant.entity.TagFieldName;
import com.epam.esm.constant.entity.UserFieldName;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dto.Order;
import com.epam.esm.dto.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao<Tag> {
    private EntityManager manager;

    @PersistenceContext
    public void setManager(EntityManager manager) {
        this.manager = manager;
    }

    @Transactional
    @Override
    public long insert(Tag tag) {
        manager.persist(tag);
        return tag.getId();
    }

    @Override
    public Optional<Tag> findById(long id) {
        return Optional.ofNullable(manager.find(Tag.class, id));
    }

    @Override
    public Optional<Tag> findByName(String name) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
        Root<Tag> root = criteria.from(Tag.class);
        criteria.where(builder.equal(root.get(TagFieldName.NAME), name));
        return manager.createQuery(criteria).getResultStream().findFirst();
    }

    @Override
    public Optional<Tag> findMostUsedTagOfUserWithHighestCostOfAllOrders(long userId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
        Root<Order> root = criteria.from(Order.class);

        Join<Tag, Order> tagOrderJoin = root.join(OrderFieldName.GIFT_CERTIFICATE)
                .join(GiftCertificateFieldName.TAGS);

        criteria.select(root.get(OrderFieldName.GIFT_CERTIFICATE).get(GiftCertificateFieldName.TAGS))
                .where(builder.equal(root.get(OrderFieldName.USER).get(UserFieldName.ID), userId))
                .groupBy(tagOrderJoin.get(TagFieldName.NAME))
                .orderBy(builder.desc(builder.count(tagOrderJoin.get(TagFieldName.NAME))),
                        builder.desc(builder.sum(root.get(OrderFieldName.PRICE))));

        return manager.createQuery(criteria).getResultStream().findFirst();
    }

    @Override
    public List<Tag> findAll(int page, int elements) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
        Root<Tag> root = criteria.from(Tag.class);
        criteria.select(root);
        return (manager.createQuery(criteria)
                .setMaxResults(elements)
                .setFirstResult(elements * (page - 1))
                .getResultList());
    }

    @Override
    public List<Tag> findAll() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
        Root<Tag> root = criteria.from(Tag.class);
        criteria.select(root);
        return manager.createQuery(criteria).getResultList();
    }

    @Transactional
    @Override
    public boolean delete(long id) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaDelete<Tag> criteria = builder.createCriteriaDelete(Tag.class);
        Root<Tag> root = criteria.from(Tag.class);
        criteria.where(builder.equal(root.get(TagFieldName.ID), id));
        return manager.createQuery(criteria).executeUpdate() == 1;
    }
}
