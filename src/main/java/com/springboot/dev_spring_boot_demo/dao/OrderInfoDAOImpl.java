package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.OrderInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OrderInfoDAOImpl implements OrderInfoDAO {

    private final EntityManager em;

    @Autowired
    public OrderInfoDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<OrderInfo> findAll() {
        TypedQuery<OrderInfo> query = em.createQuery("from OrderInfo", OrderInfo.class);
        return query.getResultList();
    }

    @Override
    public OrderInfo findById(Long id) {
        return em.find(OrderInfo.class, id);
    }

    @Override
    @Transactional
    public OrderInfo save(OrderInfo orderInfo) {
        return em.merge(orderInfo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        OrderInfo orderInfo = em.find(OrderInfo.class, id);
        if (orderInfo != null) {
            em.remove(orderInfo);
        }
    }
}
