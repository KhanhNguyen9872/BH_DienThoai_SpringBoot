package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Notification;
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
    public Page<Notification> findAll(Pageable pageable) {
        // Order notifications by time descending
        TypedQuery<Notification> query = em.createQuery("from Notification n order by n.time desc", Notification.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Notification> notifications = query.getResultList();

        // Count total notifications
        TypedQuery<Long> countQuery = em.createQuery("select count(n) from Notification n", Long.class);
        Long count = countQuery.getSingleResult();

        return new PageImpl<>(notifications, pageable, count);
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

    @Override
    @Transactional
    public void markAllAsRead() {
        em.createQuery("update Notification n set n.isRead = true where n.isRead = false")
                .executeUpdate();
    }

}
