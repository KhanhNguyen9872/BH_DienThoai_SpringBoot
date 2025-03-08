package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Admin;
import java.util.List;

public interface AdminService {
    List<Admin> findAll();
    Admin findById(Long id);
    Admin save(Admin admin);
    void deleteById(Long id);
    Admin findByUsername(String username); // new method to load admin by username
}
