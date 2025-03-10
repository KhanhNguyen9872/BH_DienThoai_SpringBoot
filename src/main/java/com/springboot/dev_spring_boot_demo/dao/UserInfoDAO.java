package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInfoDAO {
    Page<UserInfo> findAll(Pageable pageable);
    UserInfo findById(Long id);
    UserInfo save(UserInfo userInfo);
    void deleteById(Long id);
}
