package com.homifybackend.accountSetting_myListings.repository;

import com.homifybackend.accountSetting_myListings.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser_UserId(Long userId);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
}
