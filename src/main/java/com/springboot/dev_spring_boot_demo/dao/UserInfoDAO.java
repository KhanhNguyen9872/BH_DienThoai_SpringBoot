package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import java.util.List;

public interface UserInfoDAO {
    List<UserInfo> findAll();
    UserInfo findById(Long id);
    UserInfo save(UserInfo userInfo);
    void deleteById(Long id);
}
