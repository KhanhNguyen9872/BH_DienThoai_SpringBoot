package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.HistoryChatbotDAO;
import com.springboot.dev_spring_boot_demo.entity.HistoryChatbot;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HistoryChatbotServiceImpl implements HistoryChatbotService {

    private final HistoryChatbotDAO historyChatbotDAO;

    @Autowired
    public HistoryChatbotServiceImpl(HistoryChatbotDAO historyChatbotDAO) {
        this.historyChatbotDAO = historyChatbotDAO;
    }

    @Override
    public List<HistoryChatbot> findAll() {
        return historyChatbotDAO.findAll();
    }

    @Override
    public HistoryChatbot findById(Long id) {
        return historyChatbotDAO.findById(id);
    }

    @Override
    public HistoryChatbot save(HistoryChatbot historyChatbot) {
        return historyChatbotDAO.save(historyChatbot);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        historyChatbotDAO.deleteById(id);
    }
}
