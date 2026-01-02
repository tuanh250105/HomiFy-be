package com.homifybackend.salelisting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.salelisting.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
}
