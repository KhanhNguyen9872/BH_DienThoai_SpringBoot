package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Account;
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

    @Override
    public Page<Account> findAll(Pageable pageable) {
        // Query to retrieve paginated results
        TypedQuery<Account> query = em.createQuery("from Account", Account.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Account> accounts = query.getResultList();

        // Query to count total records
        Long count = em.createQuery("select count(a) from Account a", Long.class).getSingleResult();

        return new PageImpl<>(accounts, pageable, count);
    }
}
