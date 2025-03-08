package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Address;
import java.util.List;

public interface AddressDAO {
    List<Address> findAll();
    Address findById(Long id);
    Address save(Address address);
    void deleteById(Long id);
}
