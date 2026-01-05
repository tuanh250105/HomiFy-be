package com.homifybackend.accountSetting_myListings.repository;

import com.homifybackend.accountSetting_myListings.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> { }
