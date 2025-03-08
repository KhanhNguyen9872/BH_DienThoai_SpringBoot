package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProductDAOImpl implements ProductDAO {

    private final EntityManager em;

    @Autowired
    public ProductDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> query = em.createQuery("from Product", Product.class);
        return query.getResultList();
    }

    @Override
    public Product findById(int id) {
        return em.find(Product.class, id);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return em.merge(product);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Product product = em.find(Product.class, id);
        em.remove(product);
    }
}
