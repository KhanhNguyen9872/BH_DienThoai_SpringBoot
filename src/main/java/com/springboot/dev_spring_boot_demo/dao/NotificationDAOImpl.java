package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class NotificationDAOImpl implements NotificationDAO {

    private final EntityManager em;

    @Autowired
    public NotificationDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Notification> findAll() {
        TypedQuery<Notification> query = em.createQuery("from Notification", Notification.class);
        return query.getResultList();
    }

    @Override
    public Notification findById(Long id) {
        return em.find(Notification.class, id);
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        return em.merge(notification);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Notification notification = em.find(Notification.class, id);
        if (notification != null) {
            em.remove(notification);
        }
    }
}
