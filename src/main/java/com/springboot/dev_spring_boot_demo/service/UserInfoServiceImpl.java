package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.UserInfoDAO;
import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoDAO userInfoDAO;

    @Autowired
    public UserInfoServiceImpl(UserInfoDAO userInfoDAO) {
        this.userInfoDAO = userInfoDAO;
    }

    @Override
    public List<UserInfo> findAll() {
        return userInfoDAO.findAll();
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoDAO.findById(id);
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoDAO.save(userInfo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userInfoDAO.deleteById(id);
    }
}
