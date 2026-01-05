package com.homifybackend.repository.dashboard;

import com.homifybackend.model.AgentNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentNoteRepository extends JpaRepository<AgentNote, Long> {
    List<AgentNote> findByAgentIdOrderByCreatedAtDesc(Long agentId);
}