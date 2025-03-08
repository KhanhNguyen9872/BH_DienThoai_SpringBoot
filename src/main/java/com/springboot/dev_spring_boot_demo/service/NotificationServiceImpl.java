package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.NotificationDAO;
import com.springboot.dev_spring_boot_demo.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;

    @Autowired
    public NotificationServiceImpl(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public List<Notification> findAll() {
        return notificationDAO.findAll();
    }

    @Override
    public Notification findById(Long id) {
        return notificationDAO.findById(id);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationDAO.save(notification);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        notificationDAO.deleteById(id);
    }
}
