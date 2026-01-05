package com.homifybackend.repository;

import com.homifybackend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    // ✅ Load property with all features (security, entertainment, outdoor, transport, appliance ratings)
    @Query("SELECT p FROM Property p " +
            "LEFT JOIN FETCH p.securityFeatures " +
            "LEFT JOIN FETCH p.entertainmentFeatures " +
            "LEFT JOIN FETCH p.outdoorFeatures " +
            "LEFT JOIN FETCH p.transportRating " +
            "LEFT JOIN FETCH p.applianceRating " +
            "WHERE p.propertyId = :id")
    Optional<Property> findByIdWithFeatures(@Param("id") Long id);
}
