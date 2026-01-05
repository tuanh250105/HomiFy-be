package com.homifybackend.service.agentsforcustomer;

import com.homifybackend.model.AgentContact;
import com.homifybackend.model.Agent;
import com.homifybackend.dto.AgentContactDTO;
import com.homifybackend.dto.CreateContactRequest;
import com.homifybackend.repository.AgentContactRepository;
import com.homifybackend.repository.AgentRepository;
import com.homifybackend.exception.AgentNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentContactServiceImpl implements AgentContactService {

    private final AgentContactRepository contactRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public AgentContactDTO createContact(Long agentId, CreateContactRequest request) {
        log.info("Creating contact for agent ID: {}", agentId);

        try {
            // Validate Agent exists
            Agent agent = agentRepository.findById(agentId)
                    .orElseThrow(() -> new AgentNotFoundException(agentId));

            AgentContact contact = new AgentContact();
            contact.setAgent(agent);
            contact.setSenderName(request.getSenderName());
            contact.setSenderEmail(request.getSenderEmail());
            contact.setSenderPhone(request.getSenderPhone());
            contact.setMessage(request.getMessage());

            // Set property context if provided
            if (request.getPropertyId() != null) {
                contact.setPropertyId(request.getPropertyId());
                log.debug("Contact linked to property ID: {}", request.getPropertyId());
            }

            if (request.getListingType() != null && !request.getListingType().isEmpty()) {
                contact.setListingType(request.getListingType());
                log.debug("Contact listing type: {}", request.getListingType());
            }

            contact.setStatus("UNREAD");

            AgentContact saved = contactRepository.save(contact);
            log.info("Contact created successfully with ID: {}", saved.getContactId());

            return mapToDTO(saved);
        } catch (AgentNotFoundException e) {
            log.error("Agent not found: {}", agentId);
            throw e;
        } catch (Exception e) {
            log.error("Error creating contact for agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error creating contact: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentContactDTO> getContactsByAgentId(Long agentId) {
        log.debug("Fetching all contacts for agent ID: {}", agentId);

        try {
            return contactRepository.findByAgent_UserIdOrderByCreatedAtDesc(agentId)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching contacts for agent {}: {}", agentId, e.getMessage());
            throw new RuntimeException("Error fetching contacts: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentContactDTO> getContactsByAgentIdAndStatus(Long agentId, String status) {
        log.debug("Fetching contacts for agent ID: {} with status: {}", agentId, status);

        try {
            return contactRepository.findByAgent_UserIdAndStatusOrderByCreatedAtDesc(agentId, status)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching contacts for agent {} with status {}: {}", agentId, status, e.getMessage());
            throw new RuntimeException("Error fetching contacts: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentContactDTO> getContactById(Long contactId) {
        log.debug("Fetching contact by ID: {}", contactId);

        try {
            return contactRepository.findById(contactId)
                    .map(this::mapToDTO);
        } catch (Exception e) {
            log.error("Error fetching contact {}: {}", contactId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public AgentContactDTO updateContactStatus(Long contactId, String status) {
        log.info("Updating contact ID: {} to status: {}", contactId, status);

        try {
            AgentContact contact = contactRepository.findById(contactId)
                    .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + contactId));

            contact.setStatus(status);

            if ("READ".equals(status) && contact.getReadAt() == null) {
                contact.setReadAt(LocalDateTime.now());
                log.debug("Contact marked as read at: {}", contact.getReadAt());
            }

            AgentContact updated = contactRepository.save(contact);
            return mapToDTO(updated);
        } catch (Exception e) {
            log.error("Error updating contact status for {}: {}", contactId, e.getMessage());
            throw new RuntimeException("Error updating contact status: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCountByAgentId(Long agentId) {
        try {
            Long count = contactRepository.countByAgent_UserIdAndStatus(agentId, "UNREAD");
            log.debug("Unread contacts for agent ID {}: {}", agentId, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Error counting unread contacts for agent {}: {}", agentId, e.getMessage());
            return 0L;
        }
    }

    private AgentContactDTO mapToDTO(AgentContact contact) {
        try {
            return AgentContactDTO.builder()
                    .contactId(contact.getContactId())
                    .agentId(contact.getAgent() != null ? contact.getAgent().getUserId() : null)
                    .senderName(contact.getSenderName())
                    .senderEmail(contact.getSenderEmail())
                    .senderPhone(contact.getSenderPhone())
                    .message(contact.getMessage())
                    .propertyId(contact.getPropertyId())
                    .listingType(contact.getListingType())
                    .status(contact.getStatus())
                    .createdAt(contact.getCreatedAt())
                    .readAt(contact.getReadAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping contact to DTO: {}", e.getMessage());
            throw new RuntimeException("Error mapping contact data", e);
        }
    }
}