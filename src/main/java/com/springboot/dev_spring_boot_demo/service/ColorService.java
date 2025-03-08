package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Color;
import java.util.List;

public interface ColorService {
    List<Color> findAll();
    Color findById(Long id);
    Color save(Color color);
    void deleteById(Long id);
}
