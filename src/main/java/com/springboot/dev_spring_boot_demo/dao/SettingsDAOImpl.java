package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Setting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SettingsDAOImpl implements SettingsDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Setting> findAll() {
        return em.createQuery("FROM Setting", Setting.class).getResultList();
    }

    @Override
    public Setting findByKey(String key) {
        return em.find(Setting.class, key);
    }

    @Override
    @Transactional
    public Setting save(Setting setting) {
        return em.merge(setting);
    }

    @Override
    @Transactional
    public void update(String key, String value) {
        Setting setting = findByKey(key);
        if (setting != null) {
            setting.setValue(value);
            em.merge(setting);
        }
    }
}
