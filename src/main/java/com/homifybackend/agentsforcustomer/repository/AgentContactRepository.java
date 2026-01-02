package com.homifybackend.agentsforcustomer.repository;

import com.homifybackend.model.AgentContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgentContactRepository extends JpaRepository<AgentContact, Long> {

    List<AgentContact> findByAgent_UserIdOrderByCreatedAtDesc(Long agentId);

    List<AgentContact> findByAgent_UserIdAndStatusOrderByCreatedAtDesc(Long agentId, String status);

    Long countByAgent_UserIdAndStatus(Long agentId, String status);
}