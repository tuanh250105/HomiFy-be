package com.homifybackend.saved_homes.repository;

import com.homifybackend.model.CustomerFavorite;
import com.homifybackend.model.CustomerFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerFavoriteRepository extends JpaRepository<CustomerFavorite, CustomerFavoriteId> {
    void deleteByCustomerIdAndPropertyId(Long customerId, Long propertyId);
    boolean existsByCustomerIdAndPropertyId(Long customerId, Long propertyId);
}
