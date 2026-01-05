package com.homifybackend.service.agentsforcustomer;

import com.homifybackend.dto.AgentListingDTO;
import com.homifybackend.exception.AgentNotFoundException;
import com.homifybackend.model.Agent;
import com.homifybackend.repository.AgentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentListingServiceImpl implements AgentListingService {

    private final EntityManager entityManager;
    private final AgentRepository agentRepository;

    private static final String LISTING_QUERY = """
        SELECT
            sl.id as listing_id,
            sl.property_id,
            sl.sale_status,
            sl.current_price,
            sl.date_listed,
            p.beds,
            p.baths,
            p.area,
            p.description,
            a.street,
            a.city,
            a.province,
            a.zip_code,
            a.latitude,
            a.longitude,
            (SELECT url FROM listing_images WHERE listing_id = sl.id AND is_primary = true LIMIT 1) as primary_image
        FROM sale_listings sl
        JOIN properties p ON sl.property_id = p.property_id
        JOIN addresses a ON p.address_id = a.address_id
        WHERE sl.agent_id = :agentId
        %s
        ORDER BY
            CASE sl.sale_status
                WHEN 'ACTIVE' THEN 1
                WHEN 'PENDING' THEN 2
                WHEN 'SOLD' THEN 3
            END,
            sl.date_listed DESC
        """;

    @Override
    public List<AgentListingDTO> getAgentListings(Long agentId, String status) {
        // Verify agent exists
        if (!agentRepository.existsById(agentId)) {
            throw new AgentNotFoundException("Agent not found with ID: " + agentId);
        }

        // Build status filter
        String statusFilter = "";
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            statusFilter = "AND sl.sale_status = :status";
        }

        // Execute query
        String sql = String.format(LISTING_QUERY, statusFilter);
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("agentId", agentId);

        if (status != null && !status.equalsIgnoreCase("ALL")) {
            query.setParameter("status", status.toUpperCase());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToListingDTO)
                .collect(Collectors.toList());
    }
//
//    @Override
//    public AgentListingsMapResponse getAgentListingsForMap(Long agentId) {
//        // Get agent info
//        Agent agent = agentRepository.findById(agentId)
//                .orElseThrow(() -> new AgentNotFoundException("Agent not found with ID: " + agentId));
//
//        // Get all listings
//        List<AgentListingDTO> listings = getAgentListings(agentId, "ALL");
//
//        // Build response
//        AgentListingsMapResponse response = new AgentListingsMapResponse();
//
//        // Agent basic info
//        AgentListingsMapResponse.AgentBasicInfo agentInfo = new AgentListingsMapResponse.AgentBasicInfo();
//        agentInfo.setAgentId(agent.getUserId());
//        agentInfo.setFullName(agent.getFullName());
//        agentInfo.setPhoneNumber(agent.getPhoneNumber());
//
//        // Safely get email
//        if (agent.getAccount() != null) {
//            agentInfo.setEmail(agent.getAccount().getEmail());
//        }
//
//        response.setAgent(agentInfo);
//
//        // Listings
//        response.setListings(listings);
//
//        // Summary
//        AgentListingsMapResponse.ListingSummary summary = new AgentListingsMapResponse.ListingSummary();
//        summary.setForSale((int) listings.stream().filter(l -> "ACTIVE".equals(l.getStatus())).count());
//        summary.setSold((int) listings.stream().filter(l -> "SOLD".equals(l.getStatus())).count());
//        summary.setTotal(listings.size());
//        response.setSummary(summary);
//
//        return response;
//    }

    private AgentListingDTO mapToListingDTO(Object[] row) {
        AgentListingDTO dto = new AgentListingDTO();

        // ID fields
        if (row[0] != null) {
            dto.setListingId(((Number) row[0]).longValue());
        }

        if (row[1] != null) {
            dto.setPropertyId(((Number) row[1]).longValue());
        }

        // Status
        if (row[2] != null) {
            dto.setStatus(row[2].toString());
        }

        // Price - Convert to BigDecimal
        if (row[3] != null) {
            dto.setPrice(BigDecimal.valueOf(((Number) row[3]).doubleValue()));
        }

        // Date - Handle both LocalDateTime and Timestamp
        if (row[4] != null) {
            if (row[4] instanceof java.sql.Timestamp) {
                dto.setDateListed(((java.sql.Timestamp) row[4]).toLocalDateTime());
            } else if (row[4] instanceof LocalDateTime) {
                dto.setDateListed((LocalDateTime) row[4]);
            }
        }

        // Beds - Convert to Integer
        if (row[5] != null) {
            dto.setBeds(((Number) row[5]).intValue());
        }

        // Baths
        if (row[6] != null) {
            dto.setBaths(((Number) row[6]).doubleValue());
        }

        // Area
        if (row[7] != null) {
            dto.setArea(((Number) row[7]).doubleValue());
        }

        // Description
        if (row[8] != null) {
            dto.setDescription(row[8].toString());
        }

        // Address fields
        if (row[9] != null) {
            dto.setStreet(row[9].toString());
        }

        if (row[10] != null) {
            dto.setCity(row[10].toString());
        }

        if (row[11] != null) {
            dto.setProvince(row[11].toString());
        }

        if (row[12] != null) {
            dto.setZipCode(row[12].toString());
        }

        // Coordinates
        if (row[13] != null) {
            dto.setLatitude(((Number) row[13]).doubleValue());
        }

        if (row[14] != null) {
            dto.setLongitude(((Number) row[14]).doubleValue());
        }

        // Image
        if (row[15] != null) {
            dto.setPrimaryImageUrl(row[15].toString());
        }

        return dto;
    }
}