package com.homifybackend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.homifybackend.model.SaleListingDailyStats;

public interface SaleListingDailyStatsRepository extends JpaRepository<SaleListingDailyStats, Long> {
    
    @Query("SELECT s.statDate, SUM(s.views) FROM SaleListingDailyStats s WHERE s.saleListingId IN (SELECT sl.id FROM SaleListing sl WHERE sl.agent.userId = :agentId) AND s.statDate >= :startDate GROUP BY s.statDate")
    List<Object[]> sumViewsByDate(@Param("agentId") Long agentId, @Param("startDate") LocalDate startDate);
}