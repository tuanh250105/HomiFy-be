package com.homifybackend.salelisting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.salelisting.model.Villa;

@Repository
public interface VillaRepository extends JpaRepository<Villa, Long> {
}
