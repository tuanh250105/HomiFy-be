package com.homifybackend.service.agentsforcustomer;

import com.homifybackend.dto.AgentListingDTO;

import java.util.List;

/**
 * Service interface for agent listings operations
 */
public interface AgentListingService {
    
    /**
     * Get agent's listings filtered by status
     * @param agentId Agent ID
     * @param status Filter by status: "ACTIVE", "SOLD", "ALL" (default)
     * @return List of listings
     */
    List<AgentListingDTO> getAgentListings(Long agentId, String status);
    
    /**
     * Get agent's listings with coordinates for map display
     * @param agentId Agent ID
     * @return Response with agent info, listings, and summary
     */
    AgentListingsMapResponse getAgentListingsForMap(Long agentId);
}
