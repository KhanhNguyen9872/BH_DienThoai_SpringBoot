package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationService {
    List<Notification> findAll();
    Page<Notification> findAll(Pageable pageable); // New paginated method
    Notification findById(Long id);
    Notification save(Notification notification);
    void deleteById(Long id);

    // Optionally, you can add additional methods for marking notifications as read:
    boolean markAsRead(Long id);
    void markAllAsRead();
}
