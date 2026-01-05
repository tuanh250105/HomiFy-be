package com.homifybackend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.homifybackend.model.SaleListingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.SaleListing;

@Repository
public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {
    Optional<SaleListing> findByPropertyPropertyId(Long propertyId);
    List<SaleListing> findByAgentUserId(Long agentId);
    // ✅ Fix: Đổi kiểu tham số từ String sang Enum
    long countByAgent_UserIdAndSaleStatus(Long agentId, SaleListingStatus saleStatus);

    List<SaleListing> findByAgent_UserId(Long agentId);

    @Query("SELECT sl.dateListed, COUNT(sl) FROM SaleListing sl WHERE sl.agent.userId = :agentId AND sl.dateListed >= :startDate GROUP BY sl.dateListed")
    List<Object[]> countNewListingsByDate(@Param("agentId") Long agentId, @Param("startDate") LocalDateTime startDate);


    // Optimized query with eager fetch for single listing
    @EntityGraph(attributePaths = {
            "property",
            "property.address",
            "property.owner",
            "agent"
    })
    @Query("SELECT sl FROM SaleListing sl WHERE sl.id = :id")
    Optional<SaleListing> findByIdWithDetails(@Param("id") Long id);

    // Optimized query with eager fetch for all listings
    @EntityGraph(attributePaths = {
            "property",
            "property.address",
            "property.owner",
            "agent"
    })
    @Query("SELECT sl FROM SaleListing sl")
    List<SaleListing> findAllWithDetails();
}
