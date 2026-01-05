package com.homifybackend.repository;

import com.homifybackend.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByRequesterUserIdAndStatus(Long userId, String status);
    boolean existsByDateAndTimeAndStatusIgnoreCaseAndIdNot(String date, String time, String status, Long id);
}