package com.homifybackend.agentsforcustomer.controller;

import com.homifybackend.agentsforcustomer.dto.AgentDTO;
import com.homifybackend.agentsforcustomer.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
//REMOVED @CrossOrigin - using global CORS config in SecurityConfig
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAgents() {
        try {
            log.info("GET /api/agents - Fetching all agents");
            List<AgentDTO> agents = agentService.getAllAgents();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Agents retrieved successfully",
                    "data", agents,
                    "total", agents.size()
            ));
        } catch (Exception e) {
            log.error("Error fetching agents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching agents: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAgentById(@PathVariable Long id) {
        try {
            log.info("GET /api/agents/{} - Fetching agent", id);
            return agentService.getAgentById(id)
                    .map(agent -> {
                        log.debug("Agent found: {}", agent.getFullName());
                        return ResponseEntity.ok(Map.of(
                                "success", true,
                                "message", "Agent found",
                                "data", agent
                        ));
                    })
                    .orElseGet(() -> {
                        log.warn("Agent not found with ID: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "success", false,
                                        "message", "Agent not found with ID: " + id
                                ));
                    });
        } catch (Exception e) {
            log.error("Error fetching agent with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching agent: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAgents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String zipCode,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Double minRating) {
        try {
            log.info("GET /api/agents/search - keyword: {}, zipCode: {}, specialty: {}, minRating: {}",
                    keyword, zipCode, specialty, minRating);

            List<AgentDTO> agents = agentService.searchAgents(keyword, zipCode, specialty, minRating);

            Map<String, Object> searchInfo = Map.of(
                    "keyword", keyword != null ? keyword : "",
                    "zipCode", zipCode != null ? zipCode : "",
                    "specialty", specialty != null ? specialty : "",
                    "minRating", minRating != null ? minRating : 0.0
            );

            log.info("Search completed - {} agents found", agents.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Search completed",
                    "data", agents,
                    "total", agents.size(),
                    "searchCriteria", searchInfo
            ));
        } catch (Exception e) {
            log.error("Error searching agents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error searching agents: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedAgents(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            log.info("GET /api/agents/top-rated - limit: {}", limit);

            if (limit < 1 || limit > 20) {
                log.warn("Invalid limit: {}. Must be between 1 and 20", limit);
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Limit must be between 1 and 20"
                        ));
            }

            List<AgentDTO> agents = agentService.getTopRatedAgents(limit);
            log.info("Found {} top rated agents", agents.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Top rated agents retrieved",
                    "data", agents,
                    "total", agents.size()
            ));
        } catch (Exception e) {
            log.error("Error fetching top rated agents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching top rated agents: " + e.getMessage()
                    ));
        }
    }
}