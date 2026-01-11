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
    List<SaleListing> findByAgentUserId(Long agentId);
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


    @Query("SELECT s FROM SaleListing s LEFT JOIN FETCH s.images WHERE s.id = :id")
    Optional<SaleListing> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT sl FROM SaleListing sl " +
            "LEFT JOIN FETCH sl.property p " +
            "LEFT JOIN FETCH p.address " +
            "LEFT JOIN FETCH p.owner " +
            "LEFT JOIN FETCH p.securityFeatures " +
            "LEFT JOIN FETCH p.entertainmentFeatures " +
            "LEFT JOIN FETCH p.outdoorFeatures " +
            "LEFT JOIN FETCH p.transportRating " +
            "LEFT JOIN FETCH p.applianceRating " +
            "LEFT JOIN FETCH sl.agent " +
            "WHERE sl.agent.userId = :agentId")
    List<SaleListing> findByAgent_UserIdWithFeatures(@Param("agentId") Long agentId);}