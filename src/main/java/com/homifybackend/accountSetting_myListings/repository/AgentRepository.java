package com.homifybackend.accountSetting_myListings.repository;

import com.homifybackend.accountSetting_myListings.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
}
