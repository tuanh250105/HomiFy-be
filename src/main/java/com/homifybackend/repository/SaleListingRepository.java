package com.homifybackend.repository;

import com.homifybackend.model.SaleListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {

    @Query("SELECT s FROM SaleListing s " +
            "JOIN FETCH s.agent a " +
            "JOIN FETCH a.account " +
            "WHERE a.userId = :agentId")
    List<SaleListing> findByAgent_UserId(@Param("agentId") Long agentId);

    // ✅ Load listing + images (avoid LazyInitializationException when update/save images)
    @Query("SELECT s FROM SaleListing s LEFT JOIN FETCH s.images WHERE s.id = :id")
    Optional<SaleListing> findByIdWithImages(@Param("id") Long id);
}
