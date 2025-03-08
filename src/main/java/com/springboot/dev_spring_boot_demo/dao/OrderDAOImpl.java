package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Order> findAll() {
        TypedQuery<Order> query = em.createQuery("from Order", Order.class);
        return query.getResultList();
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
