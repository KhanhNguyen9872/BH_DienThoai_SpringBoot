package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDAO {
    Page<Order> findAll(Pageable pageable);
    Order findById(Long id);
    Order save(Order order);
    void deleteById(Long id);
}
