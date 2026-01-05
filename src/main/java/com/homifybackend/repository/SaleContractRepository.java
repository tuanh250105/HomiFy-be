package com.homifybackend.repository;

import com.homifybackend.model.SaleContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleContractRepository extends JpaRepository<SaleContract, Long> {

    // Đếm số hợp đồng: Cái này OK, giữ nguyên JPQL cũng được
    @Query("SELECT COUNT(sc) FROM SaleContract sc WHERE sc.saleListing.agent.userId = :agentId")
    long countByAgentId(@Param("agentId") Long agentId);

    // --- SỬA LẠI HÀM NÀY DÙNG SQL THUẦN (NATIVE QUERY) ---
    // Lý do: Postgres tính trừ ngày tháng ra số ngày rất dễ, còn JPQL thì bị lỗi Duration.
    @Query(value = """
        SELECT COALESCE(AVG(sc.contract_date - CAST(sl.date_listed AS DATE)), 0)
        FROM sale_contracts sc
        JOIN sale_listings sl ON sc.listing_id = sl.id
        WHERE sl.agent_id = :agentId
    """, nativeQuery = true)
    Double getAvgDaysToSell(@Param("agentId") Long agentId);
}