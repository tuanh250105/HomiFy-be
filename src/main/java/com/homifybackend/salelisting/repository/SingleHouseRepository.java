package com.homifybackend.salelisting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.salelisting.model.SingleHouse;

@Repository
public interface SingleHouseRepository extends JpaRepository<SingleHouse, Long> {
}
