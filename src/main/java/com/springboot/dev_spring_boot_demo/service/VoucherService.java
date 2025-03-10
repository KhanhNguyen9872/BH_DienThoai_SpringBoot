package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoucherService {
    List<Voucher> findAll();
    Voucher findById(Long id);
    Voucher save(Voucher voucher);
    void deleteById(Long id);

    // New method for pagination
    Page<Voucher> findAll(Pageable pageable);
}
