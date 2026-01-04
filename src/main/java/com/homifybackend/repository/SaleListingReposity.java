package com.homifybackend.repository;

import com.homifybackend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SaleListingReposity extends JpaRepository<SaleListing, Long> {

    @Query("""
        SELECT s FROM SaleListing s
        JOIN s.property p
        JOIN p.address a
        WHERE a.latitude BETWEEN :minLat AND :maxLat
          AND a.longitude BETWEEN :minLng AND :maxLng
          AND s.saleStatus = :status
          AND (:minPrice IS NULL OR s.currentPrice >= :minPrice)
          AND (:maxPrice IS NULL OR s.currentPrice <= :maxPrice)
          AND (
                :propertyClass IS NULL 
                OR TYPE(p) = :propertyClass
              )
    """)
    List<SaleListing> findByMapArea(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng,

            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("propertyClass") Class<? extends Property> propertyClass,
            @Param("status") SaleListingStatus status
    );
    Optional<SaleListing> findByProperty_PropertyId(Long propertyId);
}