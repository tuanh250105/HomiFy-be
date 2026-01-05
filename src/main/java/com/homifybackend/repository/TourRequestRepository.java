package com.homifybackend.repository;

import com.homifybackend.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourRequestRepository extends JpaRepository<Tour, Long> {
    
    // Đếm số tour request hôm nay của agent
    // Lưu ý: date trong Entity là String, nên tham số truyền vào cũng phải là String (yyyy-MM-dd)
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.saleListing.agent.userId = :agentId AND t.date = :dateStr")
    long countTodayTours(@Param("agentId") Long agentId, @Param("dateStr") String dateStr);

    // Lấy danh sách tour request hôm nay
    @Query("SELECT t FROM Tour t JOIN FETCH t.saleListing sl JOIN FETCH sl.property p JOIN FETCH p.address WHERE sl.agent.userId = :agentId AND t.date = :dateStr")
    List<Tour> findTodayTours(@Param("agentId") Long agentId, @Param("dateStr") String dateStr);
}