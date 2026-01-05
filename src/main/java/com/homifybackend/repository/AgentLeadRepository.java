package com.homifybackend.repository;

import com.homifybackend.model.AgentLead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentLeadRepository extends JpaRepository<AgentLead, Long> {
    long countByAgentIdAndLeadStatus(Long agentId, String leadStatus);
    
    Optional<AgentLead> findByAgentIdAndCustomerId(Long agentId, Long customerId);
}