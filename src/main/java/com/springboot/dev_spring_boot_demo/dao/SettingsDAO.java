package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Setting;
import java.util.List;

public interface SettingsDAO {
    List<Setting> findAll();
    Setting findByKey(String key);
    Setting save(Setting setting);
    void update(String key, String value);
}
