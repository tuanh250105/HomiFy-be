package com.homifybackend.repository;
import com.homifybackend.model.AgentReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgentReviewRepository extends JpaRepository<AgentReview, Long> {

    // FIXED: Use native query with explicit column selection to avoid Customer.is_favorite
    @Query(value = """
        SELECT 
            ar.review_id,
            ar.agent_id,
            ar.reviewer_id,
            ar.rating,
            ar.title,
            ar.content,
            ar.responsiveness,
            ar.local_knowledge,
            ar.negotiation_skills,
            ar.professionalism,
            ar.image_urls,
            ar.status,
            ar.review_date,
            ar.approved_at,
            u.full_name as reviewer_name,
            u.avatar_url as reviewer_avatar
        FROM agent_reviews ar
        LEFT JOIN users u ON ar.reviewer_id = u.user_id
        WHERE ar.agent_id = :agentId 
        AND ar.status = :status
        ORDER BY ar.review_date DESC
    """, nativeQuery = true)
    List<Object[]> findByAgentIdAndStatusNative(
            @Param("agentId") Long agentId,
            @Param("status") String status
    );

    @Query(value = """
        SELECT 
            ar.review_id,
            ar.agent_id,
            ar.reviewer_id,
            ar.rating,
            ar.title,
            ar.content,
            ar.responsiveness,
            ar.local_knowledge,
            ar.negotiation_skills,
            ar.professionalism,
            ar.image_urls,
            ar.status,
            ar.review_date,
            ar.approved_at,
            u.full_name as reviewer_name,
            u.avatar_url as reviewer_avatar
        FROM agent_reviews ar
        LEFT JOIN users u ON ar.reviewer_id = u.user_id
        WHERE ar.agent_id = :agentId
        ORDER BY ar.review_date DESC
    """, nativeQuery = true)
    List<Object[]> findByAgentIdNative(@Param("agentId") Long agentId);

    Long countByAgent_UserIdAndStatus(Long agentId, String status);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM AgentReview r WHERE r.agent.userId = :agentId AND r.status = 'APPROVED'")
    Double findAverageRatingByAgentId(@Param("agentId") Long agentId);
}