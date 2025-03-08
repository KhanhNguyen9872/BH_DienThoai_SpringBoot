package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.HistoryChatbot;
import java.util.List;

public interface HistoryChatbotService {
    List<HistoryChatbot> findAll();
    HistoryChatbot findById(Long id);
    HistoryChatbot save(HistoryChatbot historyChatbot);
    void deleteById(Long id);
}
