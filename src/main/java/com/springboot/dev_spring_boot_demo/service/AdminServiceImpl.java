package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.AdminDAO;
import com.springboot.dev_spring_boot_demo.entity.Admin;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminDAO adminDAO;

    @Autowired
    public AdminServiceImpl(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @Override
    public List<Admin> findAll() {
        return adminDAO.findAll();
    }

    @Override
    public Page<Admin> findAll(Pageable pageable) {
        return adminDAO.findAll(pageable);
    }

    @Override
    public Admin findById(Long id) {
        return adminDAO.findById(id);
    }

    @Override
    @Transactional  // Ensure this operation runs in a transactional context
    public Admin save(Admin admin) {
        return adminDAO.save(admin);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        adminDAO.deleteById(id);
    }

    @Override
    public Admin findByUsername(String username) {
        return adminDAO.findByUsername(username);
    }
}
