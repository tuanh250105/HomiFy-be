package com.homifybackend.repository;

import com.homifybackend.model.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
  Optional<RentalContract> findByRentalListing_Id(Long rentalListingId);
}