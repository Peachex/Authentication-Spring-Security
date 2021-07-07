package com.epam.esm.dao.impl;

import com.epam.esm.constant.entity.GiftCertificateFieldName;
import com.epam.esm.constant.entity.OrderFieldName;
import com.epam.esm.constant.entity.UserFieldName;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dto.Order;
import com.epam.esm.dto.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDaoImpl implements OrderDao<Order> {
    private EntityManager manager;

    @PersistenceContext
    public void setManager(EntityManager manager) {
        this.manager = manager;
    }

    @Transactional
    @Override
    public long insert(Order order) {
        manager.persist(order);
        return order.getId();
    }

    @Override
    public List<Order> findByUserId(int page, int elements, User user) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.where(builder.equal(root.get(OrderFieldName.USER), user));
        return (manager.createQuery(criteria)
                .setMaxResults(elements)
                .setFirstResult(elements * (page - 1))
                .getResultList());
    }

    @Transactional
    @Override
    public boolean deleteByCertificateId(long certificateId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaDelete<Order> criteria = builder.createCriteriaDelete(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.where(builder.equal(root.get(OrderFieldName.GIFT_CERTIFICATE).get(GiftCertificateFieldName.ID),
                certificateId));
        return manager.createQuery(criteria).executeUpdate() > 0;
    }

    @Override
    public List<Order> findByCertificateId(long certificateId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.where(builder.equal(root.get(OrderFieldName.GIFT_CERTIFICATE).get(GiftCertificateFieldName.ID),
                certificateId));
        return manager.createQuery(criteria).getResultList();
    }

    @Override
    public Optional<Order> findById(long id) {
        return Optional.ofNullable(manager.find(Order.class, id));
    }

    @Override
    public Optional<Order> findByUserIdAndOrderId(long certificateId, long orderId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        Predicate userPredicate = builder.equal(root.get(OrderFieldName.USER).get(UserFieldName.ID), certificateId);
        Predicate orderPredicate = builder.equal(root.get(OrderFieldName.ID), orderId);
        criteria.where(userPredicate, orderPredicate);
        return manager.createQuery(criteria).getResultStream().findFirst();
    }
}
