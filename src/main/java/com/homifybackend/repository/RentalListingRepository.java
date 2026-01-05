package com.homifybackend.repository;

import com.homifybackend.model.RentalListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RentalListingRepository extends JpaRepository<RentalListing, Long> {

    // ✅ Load rental listing + images (avoid LazyInitializationException when
    // updating/returning)
    @Query("SELECT r FROM RentalListing r LEFT JOIN FETCH r.images WHERE r.id = :id")
    Optional<RentalListing> findByIdWithImages(@Param("id") Long id);
}
