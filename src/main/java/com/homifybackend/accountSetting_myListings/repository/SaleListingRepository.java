package com.homifybackend.accountSetting_myListings.repository;

import com.homifybackend.accountSetting_myListings.model.SaleListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {
    List<SaleListing> findByAgent_UserId(Long agentId);
}
