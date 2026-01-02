package com.homifybackend.manageRentalPayments.repository;

import com.homifybackend.model.RentalPaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalPaymentScheduleRepository extends JpaRepository<RentalPaymentSchedule, Long> {

  @Query("""
    select distinct s
    from RentalPaymentSchedule s
    join fetch s.rentalContract c
    join fetch c.tenant t
    join fetch c.rentalListing rl
    join fetch rl.property p
    join fetch p.address a
  """)
  List<RentalPaymentSchedule> findAllWithDetails();
}