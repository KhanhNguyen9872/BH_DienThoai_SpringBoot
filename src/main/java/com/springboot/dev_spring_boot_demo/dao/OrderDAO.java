package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Order;
import java.util.List;

public interface OrderDAO {
    List<Order> findAll();
    Order findById(Long id);
    Order save(Order order);
    void deleteById(Long id);
}
