package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.PColor;
import java.util.List;

public interface PColorDAO {
    List<PColor> findAll();
    PColor findById(int id);
    PColor save(PColor pColor);
    void deleteById(int id);
}
