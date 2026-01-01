package com.homifybackend.saved_searches.repository;

import com.homifybackend.model.SavedSearch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedSearchesRepository extends JpaRepository<SavedSearch, Long> {

    List<SavedSearch> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<SavedSearch> findByIdAndCustomerId(Long id, Long customerId);

    boolean existsByCustomerIdAndAddressText(Long customerId, String addressText);
}
