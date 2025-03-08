package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Notification;
import java.util.List;

public interface NotificationDAO {
    List<Notification> findAll();
    Notification findById(Long id);
    Notification save(Notification notification);
    void deleteById(Long id);
}
