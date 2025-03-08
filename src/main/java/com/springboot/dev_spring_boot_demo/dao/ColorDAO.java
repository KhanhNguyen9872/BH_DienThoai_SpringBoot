package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Color;
import java.util.List;

public interface ColorDAO {
    List<Color> findAll();
    Color findById(Long id);
    Color save(Color color);
    void deleteById(Long id);
}
