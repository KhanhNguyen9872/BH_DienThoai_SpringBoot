package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.ColorDAO;
import com.springboot.dev_spring_boot_demo.entity.Color;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

    private final ColorDAO colorDAO;

    @Autowired
    public ColorServiceImpl(ColorDAO colorDAO) {
        this.colorDAO = colorDAO;
    }

    @Override
    public List<Color> findAll() {
        return colorDAO.findAll();
    }

    @Override
    public Color findById(Long id) {
        return colorDAO.findById(id);
    }

    @Override
    public Color save(Color color) {
        return colorDAO.save(color);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        colorDAO.deleteById(id);
    }
}
