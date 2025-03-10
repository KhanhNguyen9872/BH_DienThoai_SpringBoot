package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.SettingsDAO;
import com.springboot.dev_spring_boot_demo.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsDAO settingsDAO;

    @Autowired
    public SettingsServiceImpl(SettingsDAO settingsDAO) {
        this.settingsDAO = settingsDAO;
    }

    @Override
    public Map<String, String> getSettings() {
        List<Setting> settings = settingsDAO.findAll();
        return settings.stream().collect(Collectors.toMap(Setting::getKey, Setting::getValue));
    }

    @Override
    public void updateSetting(String key, String value) {
        settingsDAO.update(key, value);
    }

    @Override
    @Transactional
    public void updateSettings(Map<String, String> settings) {
        settings.forEach((key, value) -> settingsDAO.update(key, value));
    }
}
