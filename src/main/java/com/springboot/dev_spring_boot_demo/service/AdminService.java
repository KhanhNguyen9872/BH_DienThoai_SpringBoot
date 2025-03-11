package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AdminService {
    List<Admin> findAll();
    Page<Admin> findAll(Pageable pageable); // New method for paginated retrieval
    Admin findById(Long id);
    Admin save(Admin admin);
    void deleteById(Long id);
    Admin findByUsername(String username);
}
