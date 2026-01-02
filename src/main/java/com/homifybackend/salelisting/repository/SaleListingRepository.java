package com.homifybackend.salelisting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.salelisting.model.SaleListing;

@Repository
public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {
    Optional<SaleListing> findByPropertyId(Long propertyId);
    List<SaleListing> findByAgentId(Long agentId);
}
