package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationDAO {
    List<Notification> findAll();
    Page<Notification> findAll(Pageable pageable); // New paginated method
    Notification findById(Long id);
    Notification save(Notification notification);
    void deleteById(Long id);
    void markAllAsRead();
}
