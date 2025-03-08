package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.AddressDAO;
import com.springboot.dev_spring_boot_demo.entity.Address;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressDAO addressDAO;

    @Autowired
    public AddressServiceImpl(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    @Override
    public List<Address> findAll() {
        return addressDAO.findAll();
    }

    @Override
    public Address findById(Long id) {
        return addressDAO.findById(id);
    }

    @Override
    public Address save(Address address) {
        return addressDAO.save(address);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        addressDAO.deleteById(id);
    }
}
