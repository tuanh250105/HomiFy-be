package com.homifybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.Villa;

@Repository
public interface VillaRepository extends JpaRepository<Villa, Long> {
}
