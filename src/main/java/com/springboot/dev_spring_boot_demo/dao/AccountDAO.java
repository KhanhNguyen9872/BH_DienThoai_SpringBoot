package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Account;
import java.util.List;

public interface AccountDAO {
    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    void deleteById(Long id);
}
