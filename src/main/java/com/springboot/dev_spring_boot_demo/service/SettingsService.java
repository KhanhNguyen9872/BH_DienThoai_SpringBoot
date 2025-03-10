package com.springboot.dev_spring_boot_demo.service;

import java.util.Map;

public interface SettingsService {
    Map<String, String> getSettings();
    void updateSetting(String key, String value);
    // Optionally, update multiple settings:
    void updateSettings(Map<String, String> settings);
}
