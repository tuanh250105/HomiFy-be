package com.homifybackend.repository;

import com.homifybackend.model.SellRequest;
import com.homifybackend.model.SellRequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellRequestRepository extends JpaRepository<SellRequest, Long> {
    // FIX: entity có field owner (Customer), không có ownerId
    List<SellRequest> findByOwner_UserIdOrderByCreatedAtDesc(Long ownerId);


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

    @EntityGraph(attributePaths = {"address", "owner"})
    @Query("""
    select sr from SellRequest sr
    join sr.address a
    where sr.status = :status
      and not exists (
        select 1 from SurveyTask st
        where st.sellRequest = sr
      )
      and (
        lower(a.city) = lower(:district)
        or lower(a.province) = lower(:district)
      )
    order by sr.createdAt desc
""")
    List<SellRequest> findOpportunityPoolByStatusAndDistrict(
            @Param("status") SellRequestStatus status,
            @Param("district") String district
    );

}
