package com.homifybackend.repository.dashboard;

import com.homifybackend.model.SaleListing;
import com.homifybackend.model.SaleListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleListingRepository extends JpaRepository<SaleListing, Long> {
    
    // ✅ Fix: Đổi kiểu tham số từ String sang Enum
    long countByAgent_UserIdAndSaleStatus(Long agentId, SaleListingStatus saleStatus);

    List<SaleListing> findByAgent_UserId(Long agentId);

    @Query("SELECT sl.dateListed, COUNT(sl) FROM SaleListing sl WHERE sl.agent.userId = :agentId AND sl.dateListed >= :startDate GROUP BY sl.dateListed")
    List<Object[]> countNewListingsByDate(@Param("agentId") Long agentId, @Param("startDate") LocalDateTime startDate);
}