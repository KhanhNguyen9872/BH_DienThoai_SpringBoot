package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {

    private final EntityManager em;

    @Autowired
    public OrderDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        String jpql = "from Order";
        TypedQuery<Order> query = em.createQuery(jpql, Order.class);
        List<Order> orders;
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            orders = query.getResultList();
        } else {
            orders = query.getResultList();
        }
        TypedQuery<Long> countQuery = em.createQuery("select count(o) from Order o", Long.class);
        Long total = countQuery.getSingleResult();
        return new PageImpl<>(orders, pageable, total);
    }

    @Override
    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    @Override
    @Transactional
    public Order save(Order order) {
        return em.merge(order);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
        }
    }
}
