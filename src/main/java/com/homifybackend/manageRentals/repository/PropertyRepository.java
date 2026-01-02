package com.homifybackend.manageRentals.repository;

import com.homifybackend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

  @Query("SELECT DISTINCT p FROM Property p " +
      "LEFT JOIN FETCH p.address " +
      "LEFT JOIN FETCH p.transportRating " +
      "LEFT JOIN FETCH p.applianceRating " +
      "LEFT JOIN FETCH p.securityFeatures " +
      "LEFT JOIN FETCH p.outdoorFeatures " +
      "LEFT JOIN FETCH p.entertainmentFeatures " +
      "LEFT JOIN FETCH p.rentalListing rl " +
      "LEFT JOIN FETCH rl.images " +
      "LEFT JOIN FETCH Apartment apt ON apt.property.propertyId = p.propertyId " +   // THÊM
      "LEFT JOIN FETCH TownHouse th ON th.property.propertyId = p.propertyId " +     // THÊM
      "LEFT JOIN FETCH SingleHouse sh ON sh.property.propertyId = p.propertyId " +   // THÊM
      "LEFT JOIN FETCH Villa v ON v.property.propertyId = p.propertyId " +           // THÊM
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
      "LEFT JOIN FETCH Apartment apt ON apt.property.propertyId = p.propertyId " +
      "LEFT JOIN FETCH TownHouse th ON th.property.propertyId = p.propertyId " +
      "LEFT JOIN FETCH SingleHouse sh ON sh.property.propertyId = p.propertyId " +
      "LEFT JOIN FETCH Villa v ON v.property.propertyId = p.propertyId " +
      "WHERE p.propertyId = :id")
  Optional<Property> findByIdWithFullRelations(@Param("id") Long id);
}