package com.homifybackend.manage_personalinfo.repository;

import org.springframework.stereotype.Repository;

import com.homifybackend.model.User;
import com.homifybackend.model.Account;
import com.homifybackend.model.Address;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Repository
public class ManagePersonalInfoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findUserById(Long userId) {
        return entityManager.find(User.class, userId);
    }

    public Account findAccountByUserId(Long userId) {
        List<Account> list = entityManager
                .createQuery("SELECT a FROM Account a WHERE a.userId = :userId", Account.class)
                .setParameter("userId", userId)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public Address findAddressById(Long addressId) {
        return entityManager.find(Address.class, addressId);
    }

    public <T> T save(T entity) {
        if (!entityManager.contains(entity)) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }
}
