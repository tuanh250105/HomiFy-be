package com.homifybackend.agentsforcustomer.repository;

import com.homifybackend.model.AgentReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgentReviewRepository extends JpaRepository<AgentReview, Long> {

    List<AgentReview> findByAgent_UserIdAndStatusOrderByReviewDateDesc(Long agentId, String status);

    List<AgentReview> findByAgent_UserIdOrderByReviewDateDesc(Long agentId);

    Long countByAgent_UserIdAndStatus(Long agentId, String status);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM AgentReview r WHERE r.agent.userId = :agentId AND r.status = 'APPROVED'")
    Double findAverageRatingByAgentId(@Param("agentId") Long agentId);
}