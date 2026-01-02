package com.homifybackend.salelisting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.salelisting.model.TownHouse;

@Repository
public interface TownHouseRepository extends JpaRepository<TownHouse, Long> {
}
