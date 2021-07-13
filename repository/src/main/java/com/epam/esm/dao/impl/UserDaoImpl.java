package com.epam.esm.dao.impl;

import com.epam.esm.constant.entity.UserFieldName;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dto.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao<User> {
    private EntityManager manager;

    @PersistenceContext
    public void setManager(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(manager.find(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root).where(builder.equal(root.get(UserFieldName.EMAIL), email));
        return manager.createQuery(criteria).getResultList().stream().findFirst();
    }

    @Override
    public List<User> findAll(int page, int elements) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root);
        return (manager.createQuery(criteria)
                .setMaxResults(elements)
                .setFirstResult(elements * (page - 1))
                .getResultList());
    }

    @Transactional
    @Override
    public long insert(User user) {
        manager.persist(user);
        return user.getId();
    }
}
