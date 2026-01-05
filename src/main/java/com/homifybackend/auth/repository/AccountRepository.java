package com.homifybackend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    /**
     * Find account by email with User fetched (JOIN FETCH to avoid LazyInitializationException)
     * DISTINCT is used to avoid duplicate results when using JOIN FETCH
     */
    @Query("SELECT DISTINCT a FROM Account a JOIN FETCH a.user WHERE a.email = :email")
    Optional<Account> findByEmailWithUser(@Param("email") String email);
    
    /**
     * Find account by username with User fetched (JOIN FETCH to avoid LazyInitializationException)
     * DISTINCT is used to avoid duplicate results when using JOIN FETCH
     */
    @Query("SELECT DISTINCT a FROM Account a JOIN FETCH a.user WHERE a.username = :username")
    Optional<Account> findByUsernameWithUser(@Param("username") String username);
}

