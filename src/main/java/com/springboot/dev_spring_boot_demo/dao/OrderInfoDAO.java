package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.OrderInfo;
import java.util.List;

public interface OrderInfoDAO {
    List<OrderInfo> findAll();
    OrderInfo findById(Long id);
    OrderInfo save(OrderInfo orderInfo);
    void deleteById(Long id);
}
