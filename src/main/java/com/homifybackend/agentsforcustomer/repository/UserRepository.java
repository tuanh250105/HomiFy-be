package com.homifybackend.agentsforcustomer.repository;

import com.homifybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Additional query methods can be added here as needed
    // For example:
    // Optional<User> findByEmail(String email);
    // List<User> findByRole(String role);
}