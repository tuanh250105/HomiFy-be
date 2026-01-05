package com.homifybackend.repository;
import com.homifybackend.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    @Query(value = """
        SELECT 
            a.user_id,
            a.license_id,
            a.bio,
            a.rate,
            u.full_name,
            u.phone_number,
            u.avatar_url,
            CAST(NULL AS varchar) as gender,
            acc.email,
            COALESCE(addr.street, '') as street,
            COALESCE(addr.city, '') as city,
            COALESCE(addr.province, '') as province,
            COALESCE(addr.zip_code, '') as zip_code,
            COALESCE(addr.nation, 'USA') as nation,
            COALESCE((SELECT COUNT(*) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0) as review_count,
            COALESCE((SELECT COUNT(*) FROM sale_listings sl WHERE sl.agent_id = a.user_id), 0) as sale_listings_count,
            0 as rental_listings_count,
            a.specialties,
            COALESCE((SELECT AVG(ar.rating) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0.0) as average_rating
        FROM agents a
        LEFT JOIN users u ON a.user_id = u.user_id
        LEFT JOIN accounts acc ON u.user_id = acc.user_id
        LEFT JOIN addresses addr ON u.address_id = addr.address_id
        ORDER BY a.rate DESC NULLS LAST
    """, nativeQuery = true)
    List<Object[]> findAllAgentsWithFullDetails();

    @Query(value = """
        SELECT 
            a.user_id,
            a.license_id,
            a.bio,
            a.rate,
            u.full_name,
            u.phone_number,
            u.avatar_url,
            CAST(NULL AS varchar) as gender,
            acc.email,
            COALESCE(addr.street, '') as street,
            COALESCE(addr.city, '') as city,
            COALESCE(addr.province, '') as province,
            COALESCE(addr.zip_code, '') as zip_code,
            COALESCE(addr.nation, 'USA') as nation,
            COALESCE((SELECT COUNT(*) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0) as review_count,
            COALESCE((SELECT COUNT(*) FROM sale_listings sl WHERE sl.agent_id = a.user_id), 0) as sale_listings_count,
            0 as rental_listings_count,
            a.specialties,
            COALESCE((SELECT AVG(ar.rating) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0.0) as average_rating
        FROM agents a
        LEFT JOIN users u ON a.user_id = u.user_id
        LEFT JOIN accounts acc ON u.user_id = acc.user_id
        LEFT JOIN addresses addr ON u.address_id = addr.address_id
        WHERE a.user_id = :agentId
    """, nativeQuery = true)
    List<Object[]> findAgentByIdWithFullDetails(@Param("agentId") Long agentId);

    @Query(value = """
        SELECT 
            a.user_id,
            a.license_id,
            a.bio,
            a.rate,
            u.full_name,
            u.phone_number,
            u.avatar_url,
            CAST(NULL AS varchar) as gender,
            acc.email,
            COALESCE(addr.street, '') as street,
            COALESCE(addr.city, '') as city,
            COALESCE(addr.province, '') as province,
            COALESCE(addr.zip_code, '') as zip_code,
            COALESCE(addr.nation, 'USA') as nation,
            COALESCE((SELECT COUNT(*) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0) as review_count,
            COALESCE((SELECT COUNT(*) FROM sale_listings sl WHERE sl.agent_id = a.user_id), 0) as sale_listings_count,
            0 as rental_listings_count,
            a.specialties,
            COALESCE((SELECT AVG(ar.rating) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0.0) as average_rating
        FROM agents a
        LEFT JOIN users u ON a.user_id = u.user_id
        LEFT JOIN accounts acc ON u.user_id = acc.user_id
        LEFT JOIN addresses addr ON u.address_id = addr.address_id
        WHERE 1=1
            AND (:keyword IS NULL OR :keyword = '' OR
                 LOWER(COALESCE(u.full_name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(COALESCE(a.bio, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(COALESCE(a.license_id, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(COALESCE(addr.city, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(COALESCE(addr.province, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 EXISTS (SELECT 1 FROM unnest(a.specialties) AS spec WHERE LOWER(spec) LIKE LOWER(CONCAT('%', :keyword, '%'))))
            AND (:zipCode IS NULL OR :zipCode = '' OR 
                 LOWER(COALESCE(addr.zip_code, '')) LIKE LOWER(CONCAT('%', :zipCode, '%')))
            AND (:specialty IS NULL OR :specialty = '' OR 
                 :specialty = ANY(a.specialties))
            AND (:minRating IS NULL OR 
                 COALESCE((SELECT AVG(ar.rating) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0.0) >= :minRating)
        ORDER BY 
            COALESCE((SELECT AVG(ar.rating) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0.0) DESC,
            a.rate DESC NULLS LAST
    """, nativeQuery = true)
    List<Object[]> searchAgentsEnhanced(
            @Param("keyword") String keyword,
            @Param("zipCode") String zipCode,
            @Param("specialty") String specialty,
            @Param("minRating") Double minRating
    );

    @Query(value = """
        SELECT 
            a.user_id,
            a.license_id,
            a.bio,
            a.rate,
            u.full_name,
            u.phone_number,
            u.avatar_url,
            CAST(NULL AS varchar) as gender,
            acc.email,
            COALESCE(addr.street, '') as street,
            COALESCE(addr.city, '') as city,
            COALESCE(addr.province, '') as province,
            COALESCE(addr.zip_code, '') as zip_code,
            COALESCE(addr.nation, 'USA') as nation,
            COALESCE((SELECT COUNT(*) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0) as review_count,
            COALESCE((SELECT COUNT(*) FROM sale_listings sl WHERE sl.agent_id = a.user_id), 0) as sale_listings_count,
            0 as rental_listings_count,
            a.specialties,
            COALESCE((SELECT AVG(ar.rating) FROM agent_reviews ar WHERE ar.agent_id = a.user_id AND ar.status = 'APPROVED'), 0.0) as average_rating
        FROM agents a
        LEFT JOIN users u ON a.user_id = u.user_id
        LEFT JOIN accounts acc ON u.user_id = acc.user_id
        LEFT JOIN addresses addr ON u.address_id = addr.address_id
        WHERE a.rate IS NOT NULL
        ORDER BY a.rate DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTopRatedAgents(@Param("limit") int limit);
}