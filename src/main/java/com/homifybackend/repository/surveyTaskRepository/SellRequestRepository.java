package com.homifybackend.repository.surveyTaskRepository;

import java.util.List;

import com.homifybackend.model.SellRequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.homifybackend.model.SellRequest;

public interface SellRequestRepository extends JpaRepository<SellRequest, Long> {

    @EntityGraph(attributePaths = {"address", "owner"})
    @Query("""
        select sr from SellRequest sr
        where sr.status = :status
          and not exists (
            select 1 from SurveyTask st
            where st.sellRequest = sr
          )
        order by sr.createdAt desc
    """)
    List<SellRequest> findOpportunityPoolByStatus(@Param("status") SellRequestStatus status);
}