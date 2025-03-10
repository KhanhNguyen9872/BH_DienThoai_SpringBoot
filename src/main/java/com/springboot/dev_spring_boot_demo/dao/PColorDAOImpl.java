package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.PColor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PColorDAOImpl implements PColorDAO {

    private final EntityManager em;

    @Autowired
    public PColorDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<PColor> findAll() {
        TypedQuery<PColor> query = em.createQuery("from PColor", PColor.class);
        return query.getResultList();
    }

    @Override
    public PColor findById(int id) {
        return em.find(PColor.class, id);
    }

    @Override
    @Transactional
    public PColor save(PColor pColor) {
        return em.merge(pColor);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        PColor pColor = em.find(PColor.class, id);
        em.remove(pColor);
    }
}
