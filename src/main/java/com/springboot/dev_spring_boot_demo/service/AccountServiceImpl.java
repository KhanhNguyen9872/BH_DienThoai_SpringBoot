package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.AccountDAO;
import com.springboot.dev_spring_boot_demo.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    @Override
    public Account findById(Long id) {
        return accountDAO.findById(id);
    }

    @Override
    public Account save(Account account) {
        return accountDAO.save(account);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        accountDAO.deleteById(id);
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return accountDAO.findAll(pageable);
    }
}
