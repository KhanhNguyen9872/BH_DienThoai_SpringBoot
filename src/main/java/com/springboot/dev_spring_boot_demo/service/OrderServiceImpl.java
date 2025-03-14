package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.OrderDAO;
import com.springboot.dev_spring_boot_demo.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;

    @Autowired
    public OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderDAO.findAll(pageable);
    }

    @Override
    public Order findById(Long id) {
        return orderDAO.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderDAO.save(order);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderDAO.deleteById(id);
    }
}
