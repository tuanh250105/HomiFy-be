package com.homifybackend.salelisting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.salelisting.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
