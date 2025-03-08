package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Admin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AdminDAOImpl implements AdminDAO {

    private final EntityManager em;

    @Autowired
    public AdminDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Admin> findAll() {
        TypedQuery<Admin> query = em.createQuery("from Admin", Admin.class);
        return query.getResultList();
    }

    @Override
    public Admin findById(Long id) {
        return em.find(Admin.class, id);
    }

    @Override
    public Admin save(Admin admin) {
        return em.merge(admin);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Admin admin = em.find(Admin.class, id);
        if (admin != null) {
            em.remove(admin);
        }
    }

    @Override
    public Admin findByUsername(String username) {
        TypedQuery<Admin> query = em.createQuery("from Admin where username = :username", Admin.class);
        query.setParameter("username", username);
        List<Admin> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
