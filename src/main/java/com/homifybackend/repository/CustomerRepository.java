package com.homifybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
