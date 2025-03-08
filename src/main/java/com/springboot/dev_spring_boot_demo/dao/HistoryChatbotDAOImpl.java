package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.HistoryChatbot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class HistoryChatbotDAOImpl implements HistoryChatbotDAO {

    private final EntityManager em;

    @Autowired
    public HistoryChatbotDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<HistoryChatbot> findAll() {
        TypedQuery<HistoryChatbot> query = em.createQuery("from HistoryChatbot", HistoryChatbot.class);
        return query.getResultList();
    }

    @Override
    public HistoryChatbot findById(Long id) {
        return em.find(HistoryChatbot.class, id);
    }

    @Override
    @Transactional
    public HistoryChatbot save(HistoryChatbot historyChatbot) {
        return em.merge(historyChatbot);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        HistoryChatbot historyChatbot = em.find(HistoryChatbot.class, id);
        if (historyChatbot != null) {
            em.remove(historyChatbot);
        }
    }
}
