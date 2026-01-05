package com.homifybackend.repository;

import com.homifybackend.model.SaleListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {
}