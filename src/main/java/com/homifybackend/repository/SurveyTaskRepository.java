package com.homifybackend.repository;

import com.homifybackend.model.SurveyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SurveyTaskRepository extends JpaRepository<SurveyTask, Long> {
    List<SurveyTask> findByAgentId(Long agentId);

    // ✅ Thêm hàm mới để query theo ngày, hiệu quả hơn
    @Query("SELECT st FROM SurveyTask st WHERE st.agentId = :agentId AND st.scheduledAt >= :startOfDay AND st.scheduledAt < :endOfDay")
    List<SurveyTask> findTasksByAgentIdAndDate(
            @Param("agentId") Long agentId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
