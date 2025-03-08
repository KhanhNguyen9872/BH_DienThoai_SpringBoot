package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Voucher;
import java.util.List;

public interface VoucherService {
    List<Voucher> findAll();
    Voucher findById(Long id);
    Voucher save(Voucher voucher);
    void deleteById(Long id);
}
