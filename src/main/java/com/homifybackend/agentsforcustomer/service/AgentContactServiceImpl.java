package com.homifybackend.agentsforcustomer.service;

import com.homifybackend.agentsforcustomer.dto.AgentContactDTO;
import com.homifybackend.agentsforcustomer.dto.CreateContactRequest;
import com.homifybackend.agentsforcustomer.repository.AgentContactRepository;
import com.homifybackend.agentsforcustomer.repository.AgentRepository;
import com.homifybackend.agentsforcustomer.exception.AgentNotFoundException;
import com.homifybackend.model.Agent;
import com.homifybackend.model.AgentContact;
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

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(agentId));

        AgentContact contact = new AgentContact();
        contact.setAgent(agent);
        contact.setSenderName(request.getSenderName());
        contact.setSenderEmail(request.getSenderEmail());
        contact.setSenderPhone(request.getSenderPhone());
        contact.setMessage(request.getMessage());

        // ✅ ADDED: Set property context if provided
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
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentContactDTO> getContactsByAgentId(Long agentId) {
        log.debug("Fetching all contacts for agent ID: {}", agentId);
        return contactRepository.findByAgent_UserIdOrderByCreatedAtDesc(agentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentContactDTO> getContactsByAgentIdAndStatus(Long agentId, String status) {
        log.debug("Fetching contacts for agent ID: {} with status: {}", agentId, status);
        return contactRepository.findByAgent_UserIdAndStatusOrderByCreatedAtDesc(agentId, status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentContactDTO> getContactById(Long contactId) {
        log.debug("Fetching contact by ID: {}", contactId);
        return contactRepository.findById(contactId)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public AgentContactDTO updateContactStatus(Long contactId, String status) {
        log.info("Updating contact ID: {} to status: {}", contactId, status);

        AgentContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setStatus(status);
        if ("READ".equals(status) && contact.getReadAt() == null) {
            contact.setReadAt(LocalDateTime.now());
            log.debug("Contact marked as read at: {}", contact.getReadAt());
        }

        AgentContact updated = contactRepository.save(contact);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCountByAgentId(Long agentId) {
        Long count = contactRepository.countByAgent_UserIdAndStatus(agentId, "UNREAD");
        log.debug("Unread contacts for agent ID {}: {}", agentId, count);
        return count != null ? count : 0L;
    }

    private AgentContactDTO mapToDTO(AgentContact contact) {
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
    }
}