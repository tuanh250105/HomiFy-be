package com.homifybackend.repository;

import com.homifybackend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalManagerRepository extends JpaRepository<Property, Long> {

  @Query("SELECT DISTINCT p FROM Property p " +
      "LEFT JOIN FETCH p.address " +
      "LEFT JOIN FETCH p.transportRating " +
      "LEFT JOIN FETCH p.applianceRating " +
      "LEFT JOIN FETCH p.securityFeatures " +
      "LEFT JOIN FETCH p.outdoorFeatures " +
      "LEFT JOIN FETCH p.entertainmentFeatures " +
      "LEFT JOIN FETCH p.rentalListing rl " +
      "LEFT JOIN FETCH rl.images " +
      // subclass-specific joins removed — inheritance mapping will populate subtype fields
      "WHERE p.owner.userId = :ownerId " +
      "ORDER BY p.createdAt DESC")
  List<Property> findAllByOwnerIdWithFullRelations(@Param("ownerId") Long ownerId);

  @Query("SELECT p FROM Property p " +
      "LEFT JOIN FETCH p.address " +
      "LEFT JOIN FETCH p.transportRating " +
      "LEFT JOIN FETCH p.applianceRating " +
      "LEFT JOIN FETCH p.securityFeatures " +
      "LEFT JOIN FETCH p.outdoorFeatures " +
      "LEFT JOIN FETCH p.entertainmentFeatures " +
      "LEFT JOIN FETCH p.rentalListing rl " +
      "LEFT JOIN FETCH rl.images " +
      // subclass-specific joins removed — inheritance mapping will populate subtype fields
      "WHERE p.propertyId = :id")
  Optional<Property> findByIdWithFullRelations(@Param("id") Long id);
}