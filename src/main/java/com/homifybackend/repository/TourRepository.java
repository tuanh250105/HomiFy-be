package com.homifybackend.repository;

import com.homifybackend.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    boolean existsByDateAndTimeAndStatusIgnoreCaseAndIdNot(
            String date,
            String time,
            String status,
            Long id);
}