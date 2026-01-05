package com.homifybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.Apartment;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
}
