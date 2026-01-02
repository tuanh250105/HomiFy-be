package com.homifybackend.accountSetting_myListings.repository;

import com.homifybackend.accountSetting_myListings.model.RentalListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalListingRepository extends JpaRepository<RentalListing, Long> { }
