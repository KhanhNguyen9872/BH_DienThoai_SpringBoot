package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import java.util.List;

public interface UserInfoService {
    List<UserInfo> findAll();
    UserInfo findById(Long id);
    UserInfo save(UserInfo userInfo);
    void deleteById(Long id);
}
