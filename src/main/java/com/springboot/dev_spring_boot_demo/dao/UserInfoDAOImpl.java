package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserInfoDAOImpl implements UserInfoDAO {

    private final EntityManager em;

    @Autowired
    public UserInfoDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<UserInfo> findAll() {
        TypedQuery<UserInfo> query = em.createQuery("from UserInfo", UserInfo.class);
        return query.getResultList();
    }

    @Override
    public UserInfo findById(Long id) {
        return em.find(UserInfo.class, id);
    }

    @Override
    @Transactional
    public UserInfo save(UserInfo userInfo) {
        return em.merge(userInfo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserInfo userInfo = em.find(UserInfo.class, id);
        if (userInfo != null) {
            em.remove(userInfo);
        }
    }
}
