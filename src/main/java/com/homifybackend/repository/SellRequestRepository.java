package com.homifybackend.explore_options.repository;

import com.homifybackend.model.SellRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellRequestRepository extends JpaRepository<SellRequest, Long> {
    // FIX: entity có field owner (Customer), không có ownerId
    List<SellRequest> findByOwner_UserIdOrderByCreatedAtDesc(Long ownerId);
}
