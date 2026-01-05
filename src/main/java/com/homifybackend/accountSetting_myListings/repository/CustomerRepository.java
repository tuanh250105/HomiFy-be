package com.homifybackend.accountSetting_myListings.repository;

import com.homifybackend.accountSetting_myListings.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
