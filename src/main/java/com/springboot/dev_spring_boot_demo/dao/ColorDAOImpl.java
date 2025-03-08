package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Color;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ColorDAOImpl implements ColorDAO {

    private final EntityManager em;

    @Autowired
    public ColorDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Color> findAll() {
        TypedQuery<Color> query = em.createQuery("from Color", Color.class);
        return query.getResultList();
    }

    @Override
    public Color findById(Long id) {
        return em.find(Color.class, id);
    }

    @Override
    @Transactional
    public Color save(Color color) {
        return em.merge(color);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Color color = em.find(Color.class, id);
        if (color != null) {
            em.remove(color);
        }
    }
}
