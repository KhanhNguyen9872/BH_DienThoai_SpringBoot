package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> findAll();
    Notification findById(Long id);
    Notification save(Notification notification);
    void deleteById(Long id);
}
