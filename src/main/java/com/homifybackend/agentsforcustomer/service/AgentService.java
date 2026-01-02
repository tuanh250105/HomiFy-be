package com.homifybackend.agentsforcustomer.service;

import com.homifybackend.agentsforcustomer.dto.AgentDTO;
import java.util.List;
import java.util.Optional;

public interface AgentService {

    List<AgentDTO> getAllAgents();

    Optional<AgentDTO> getAgentById(Long agentId);

    //ENHANCED: Complete search with all parameters
    List<AgentDTO> searchAgents(String keyword, String zipCode, String specialty, Double minRating);

    List<AgentDTO> getTopRatedAgents(int limit);
}