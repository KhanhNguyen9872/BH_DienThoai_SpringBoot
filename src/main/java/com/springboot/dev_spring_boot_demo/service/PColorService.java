package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.PColor;
import java.util.List;

public interface PColorService {
    List<PColor> findAll();
    PColor findById(Long id);
    PColor save(PColor pColor);
    void deleteById(Long id);
}
