package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Address;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AddressDAOImpl implements AddressDAO {

    private final EntityManager em;

    @Autowired
    public AddressDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Address> findAll() {
        TypedQuery<Address> query = em.createQuery("from Address", Address.class);
        return query.getResultList();
    }

    @Override
    public Address findById(Long id) {
        return em.find(Address.class, id);
    }

    @Override
    @Transactional
    public Address save(Address address) {
        return em.merge(address);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Address address = em.find(Address.class, id);
        if (address != null) {
            em.remove(address);
        }
    }
}
