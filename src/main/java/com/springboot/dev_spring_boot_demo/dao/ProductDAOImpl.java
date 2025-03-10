package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<Product> findAll(Pageable pageable) {
        String jpql = "from Product";
        TypedQuery<Product> query = em.createQuery(jpql, Product.class);
        List<Product> products;
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            products = query.getResultList();
        } else {
            products = query.getResultList();
        }

        // Count query remains the same
        TypedQuery<Long> countQuery = em.createQuery("select count(p) from Product p", Long.class);
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(products, pageable, total);
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
