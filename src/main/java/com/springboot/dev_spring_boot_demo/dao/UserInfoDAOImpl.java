package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.UserInfo;
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
public class UserInfoDAOImpl implements UserInfoDAO {

    private final EntityManager em;

    @Autowired
    public UserInfoDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<UserInfo> findAll(Pageable pageable) {
        String jpql = "from UserInfo";
        TypedQuery<UserInfo> query = em.createQuery(jpql, UserInfo.class);
        List<UserInfo> users;
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            users = query.getResultList();
        } else {
            users = query.getResultList();
        }
        TypedQuery<Long> countQuery = em.createQuery("select count(u) from UserInfo u", Long.class);
        Long total = countQuery.getSingleResult();
        return new PageImpl<>(users, pageable, total);
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
