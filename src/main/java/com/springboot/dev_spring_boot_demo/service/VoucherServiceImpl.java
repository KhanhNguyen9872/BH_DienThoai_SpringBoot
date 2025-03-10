package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.VoucherDAO;
import com.springboot.dev_spring_boot_demo.entity.Voucher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherDAO voucherDAO;

    @Autowired
    public VoucherServiceImpl(VoucherDAO voucherDAO) {
        this.voucherDAO = voucherDAO;
    }

    @Override
    public List<Voucher> findAll() {
        return voucherDAO.findAll();
    }

    @Override
    public Voucher findById(Long id) {
        return voucherDAO.findById(id);
    }

    @Override
    public Voucher save(Voucher voucher) {
        return voucherDAO.save(voucher);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        voucherDAO.deleteById(id);
    }

    @Override
    public Page<Voucher> findAll(Pageable pageable) {
        return voucherDAO.findAll(pageable);
    }
}
