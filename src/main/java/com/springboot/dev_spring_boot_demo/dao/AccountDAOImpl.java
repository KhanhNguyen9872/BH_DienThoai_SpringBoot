package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AccountDAOImpl implements AccountDAO {

    private final EntityManager em;

    @Autowired
    public AccountDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Account> findAll() {
        TypedQuery<Account> query = em.createQuery("from Account", Account.class);
        return query.getResultList();
    }

    @Override
    public Account findById(Long id) {
        return em.find(Account.class, id);
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return em.merge(account);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Account account = em.find(Account.class, id);
        if (account != null) {
            em.remove(account);
        }
    }
}
