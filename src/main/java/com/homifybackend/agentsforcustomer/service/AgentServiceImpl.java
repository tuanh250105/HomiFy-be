package com.homifybackend.agentsforcustomer.service;

import com.homifybackend.agentsforcustomer.dto.AgentDTO;
import com.homifybackend.agentsforcustomer.mapper.AgentMapper;
import com.homifybackend.agentsforcustomer.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;

    @Override
    public List<AgentDTO> getAllAgents() {
        log.debug("Fetching all agents");
        List<Object[]> results = agentRepository.findAllAgentsWithFullDetails();
        log.info("Found {} agents", results.size());
        return results.stream()
                .map(agentMapper::mapToDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AgentDTO> getAgentById(Long agentId) {
        log.debug("Fetching agent by ID: {}", agentId);
        List<Object[]> results = agentRepository.findAgentByIdWithFullDetails(agentId);
        return results.stream()
                .findFirst()
                .map(agentMapper::mapToDTO);
    }

    // ✅ ENHANCED: Complete search implementation
    @Override
    public List<AgentDTO> searchAgents(String keyword, String zipCode, String specialty, Double minRating) {
        log.info("Searching agents - keyword: {}, zipCode: {}, specialty: {}, minRating: {}",
                keyword, zipCode, specialty, minRating);

        // If all parameters are null/empty, return all agents
        boolean hasNoFilters = (keyword == null || keyword.trim().isEmpty()) &&
                (zipCode == null || zipCode.trim().isEmpty()) &&
                (specialty == null || specialty.trim().isEmpty()) &&
                (minRating == null || minRating == 0.0);

        if (hasNoFilters) {
            log.debug("No search filters provided, returning all agents");
            return getAllAgents();
        }

        // Use enhanced search query
        List<Object[]> results = agentRepository.searchAgentsEnhanced(
                keyword != null ? keyword.trim() : null,
                zipCode != null ? zipCode.trim() : null,
                specialty != null ? specialty.trim() : null,
                minRating
        );

        log.info("Search returned {} results", results.size());

        return results.stream()
                .map(agentMapper::mapToDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> getTopRatedAgents(int limit) {
        log.debug("Fetching top {} rated agents", limit);
        List<Object[]> results = agentRepository.findTopRatedAgents(limit);
        log.info("Found {} top rated agents", results.size());
        return results.stream()
                .map(agentMapper::mapToDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}