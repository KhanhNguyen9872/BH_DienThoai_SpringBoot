package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Voucher;
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
public class VoucherDAOImpl implements VoucherDAO {

    private final EntityManager em;

    @Autowired
    public VoucherDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Voucher> findAll() {
        TypedQuery<Voucher> query = em.createQuery("from Voucher", Voucher.class);
        return query.getResultList();
    }

    @Override
    public Voucher findById(Long id) {
        return em.find(Voucher.class, id);
    }

    @Override
    @Transactional
    public Voucher save(Voucher voucher) {
        return em.merge(voucher);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Voucher voucher = em.find(Voucher.class, id);
        if (voucher != null) {
            em.remove(voucher);
        }
    }

    @Override
    public Page<Voucher> findAll(Pageable pageable) {
        TypedQuery<Voucher> query = em.createQuery("from Voucher", Voucher.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Voucher> vouchers = query.getResultList();

        // Count total vouchers
        Long count = em.createQuery("select count(v) from Voucher v", Long.class).getSingleResult();

        return new PageImpl<>(vouchers, pageable, count);
    }
}
