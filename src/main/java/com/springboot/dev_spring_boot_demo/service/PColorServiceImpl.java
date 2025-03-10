package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.PColorDAO;
import com.springboot.dev_spring_boot_demo.entity.PColor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PColorServiceImpl implements PColorService {

    private final PColorDAO pColorDAO;

    @Autowired
    public PColorServiceImpl(PColorDAO pColorDAO) {
        this.pColorDAO = pColorDAO;
    }

    @Override
    public List<PColor> findAll() {
        return pColorDAO.findAll();
    }

    @Override
    public PColor findById(Long id) {
        // Convert Long to int since our DAO methods use int
        return pColorDAO.findById(id.intValue());
    }

    @Override
    public PColor save(PColor pColor) {
        return pColorDAO.save(pColor);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Convert Long to int since our DAO methods use int
        pColorDAO.deleteById(id.intValue());
    }
}
