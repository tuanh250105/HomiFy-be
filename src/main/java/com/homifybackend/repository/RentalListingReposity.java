package com.homifybackend.repository;

import com.homifybackend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface RentalListingReposity extends JpaRepository<RentalListing, Long> {
    @Query("""
        SELECT r FROM RentalListing r
        JOIN r.property p
        JOIN p.address a
        WHERE a.latitude BETWEEN :minLat AND :maxLat
          AND a.longitude BETWEEN :minLng AND :maxLng
          AND r.rentalStatus = :status
          AND (:minRent IS NULL OR r.monthlyRent >= :minPrice)
          AND (:maxRent IS NULL OR r.monthlyRent <= :maxPrice)
          AND (
                :propertyClass IS NULL
                OR TYPE(p) = :propertyClass
              )
    """)
    List<RentalListing> findByMapArea(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng,
            @Param("minRent") Double minPrice,
            @Param("maxRent") Double maxPrice,
            @Param("propertyClass") Class<? extends Property> propertyClass,
            @Param("status") RentalListingStatus status
    );

    Optional<RentalListing> findByProperty_PropertyId(Long propertyId);
}
