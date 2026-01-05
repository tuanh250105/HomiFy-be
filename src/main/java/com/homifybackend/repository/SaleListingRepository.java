package com.homifybackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.SaleListing;

@Repository
public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {
    Optional<SaleListing> findByPropertyPropertyId(Long propertyId);
    List<SaleListing> findByAgentUserId(Long agentId);
}
