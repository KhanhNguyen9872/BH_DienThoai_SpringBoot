package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductDAO {
    Page<Product> findAll(Pageable pageable);
    Product findById(int id);
    Product save(Product product);
    void deleteById(int id);
}
