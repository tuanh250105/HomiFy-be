package com.homifybackend.manage_personalinfo.repository;

import com.homifybackend.model.Account;
import com.homifybackend.model.Address;
import com.homifybackend.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ManagePersonalInfoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findUserById(Long userId) {
        return entityManager.find(User.class, userId);
    }

    public Account findAccountByUserId(Long userId) {
        TypedQuery<Account> q = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.user.userId = :userId",
                Account.class
        );
        q.setParameter("userId", userId);

        List<Account> rs = q.getResultList();
        return rs.isEmpty() ? null : rs.get(0);
    }

    public Address findAddressById(Long addressId) {
        return entityManager.find(Address.class, addressId);
    }

    public User saveUser(User user) {
        // user lấy ra từ find() thường là managed, merge vẫn an toàn
        return entityManager.merge(user);
    }

    public Account saveAccount(Account account) {
        return entityManager.merge(account);
    }

    public Address saveAddress(Address address) {
        // Address mới => persist, có id => merge
        try {
            // nếu entity có getter getAddressId()
            Long id = address.getAddressId();
            if (id == null) {
                entityManager.persist(address);
                return address;
            }
            return entityManager.merge(address);
        } catch (Exception e) {
            // fallback nếu Address của bạn không dùng getAddressId()
            return entityManager.merge(address);
        }
    }
}
