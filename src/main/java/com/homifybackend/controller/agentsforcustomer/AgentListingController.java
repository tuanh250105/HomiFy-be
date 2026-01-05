package com.homifybackend.controller.agentsforcustomer;

import com.homifybackend.dto.AgentListingDTO;
import com.homifybackend.exception.AgentNotFoundException;
import com.homifybackend.service.agentsforcustomer.AgentListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for agent listings endpoints
 * Add these methods to your existing AgentController
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AgentListingController {

    private final AgentListingService agentListingService;

    /**
     * GET /api/agents/{agentId}/listings?status=ACTIVE
     * 
     * Get agent's listings filtered by status
     * 
     * @param agentId Agent ID
     * @param status Filter: "ACTIVE", "SOLD", or "ALL" (default)
     * @return List of listings
     * 
     * Example requests:
     * - GET /api/agents/51/listings - Get all listings
     * - GET /api/agents/51/listings?status=ACTIVE - Get For Sale listings
     * - GET /api/agents/51/listings?status=SOLD - Get Sold listings
     */
    @GetMapping("/{agentId}/listings")
    public ResponseEntity<?> getAgentListings(
            @PathVariable Long agentId,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        
        try {
            List<AgentListingDTO> listings = agentListingService.getAgentListings(agentId, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", listings);
            response.put("count", listings.size());
            
            return ResponseEntity.ok(response);
            
        } catch (AgentNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error retrieving agent listings: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/agents/{agentId}/listings-map
     * 
     * Get agent's listings with coordinates for map display
     * Returns all listings (For Sale + Sold) with lat/long coordinates
     * 
     * @param agentId Agent ID
     * @return Response with agent info, listings with coordinates, and summary stats
     * 
     * Example request:
     * - GET /api/agents/51/listings-map
     * 
     * Response includes:
     * - agent: { agentId, fullName, totalListings }
     * - listings: [{ listingId, status, price, beds, baths, latitude, longitude, ... }]
     * - summary: { forSale, sold, total }
     */
    @GetMapping("/{agentId}/listings-map")
    public ResponseEntity<?> getAgentListingsForMap(@PathVariable Long agentId) {
        
        try {
            AgentListingsMapResponse mapData = agentListingService.getAgentListingsForMap(agentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mapData);
            
            return ResponseEntity.ok(response);
            
        } catch (AgentNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error retrieving map data: " + e.getMessage()
            ));
        }
    }
}
