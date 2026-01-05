package com.homifybackend.repository.surveyTaskRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homifybackend.model.Agent;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
}