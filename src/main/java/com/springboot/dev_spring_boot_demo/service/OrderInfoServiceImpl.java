package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.OrderInfoDAO;
import com.springboot.dev_spring_boot_demo.entity.OrderInfo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    private final OrderInfoDAO orderInfoDAO;

    @Autowired
    public OrderInfoServiceImpl(OrderInfoDAO orderInfoDAO) {
        this.orderInfoDAO = orderInfoDAO;
    }

    @Override
    public List<OrderInfo> findAll() {
        return orderInfoDAO.findAll();
    }

    @Override
    public OrderInfo findById(Long id) {
        return orderInfoDAO.findById(id);
    }

    @Override
    public OrderInfo save(OrderInfo orderInfo) {
        return orderInfoDAO.save(orderInfo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderInfoDAO.deleteById(id);
    }
}
