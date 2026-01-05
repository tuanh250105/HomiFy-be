package com.homifybackend.controller.agentsforcustomer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.homifybackend.service.agentsforcustomer.AgentContactService;
import com.homifybackend.dto.CreateContactRequest;
import com.homifybackend.dto.AgentContactDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
// ✅ REMOVED @CrossOrigin - using global CORS config in SecurityConfig
public class AgentContactController {

    private final AgentContactService contactService;

    @PostMapping("/{agentId}/contact")
    public ResponseEntity<Map<String, Object>> createContact(
            @PathVariable Long agentId,
            @Valid @RequestBody CreateContactRequest request) {
        try {
            log.info("POST /api/agents/{}/contact - Creating contact", agentId);
            AgentContactDTO contact = contactService.createContact(agentId, request);
            log.info("Contact created successfully with ID: {}", contact.getContactId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Contact request sent successfully",
                            "data", contact
                    ));
        } catch (Exception e) {
            log.error("Error creating contact for agent {}", agentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error creating contact: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/{agentId}/contacts")
    public ResponseEntity<Map<String, Object>> getContactsByAgent(
            @PathVariable Long agentId,
            @RequestParam(required = false) String status) {
        try {
            log.info("GET /api/agents/{}/contacts - status: {}", agentId, status);

            List<AgentContactDTO> contacts;
            if (status != null && !status.isEmpty()) {
                contacts = contactService.getContactsByAgentIdAndStatus(agentId, status);
            } else {
                contacts = contactService.getContactsByAgentId(agentId);
            }

            Long unreadCount = contactService.getUnreadCountByAgentId(agentId);

            log.info("Found {} contacts for agent {}", contacts.size(), agentId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contacts retrieved successfully",
                    "data", contacts,
                    "total", contacts.size(),
                    "unreadCount", unreadCount
            ));
        } catch (Exception e) {
            log.error("Error fetching contacts for agent {}", agentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching contacts: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/contacts/{contactId}")
    public ResponseEntity<Map<String, Object>> getContactById(@PathVariable Long contactId) {
        try {
            log.info("GET /api/agents/contacts/{}", contactId);

            return contactService.getContactById(contactId)
                    .map(contact -> {
                        log.debug("Contact found: {}", contact.getContactId());
                        return ResponseEntity.ok(Map.of(
                                "success", true,
                                "message", "Contact found",
                                "data", contact
                        ));
                    })
                    .orElseGet(() -> {
                        log.warn("Contact not found with ID: {}", contactId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "success", false,
                                        "message", "Contact not found"
                                ));
                    });
        } catch (Exception e) {
            log.error("Error fetching contact {}", contactId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching contact: " + e.getMessage()
                    ));
        }
    }

    @PutMapping("/contacts/{contactId}/status")
    public ResponseEntity<Map<String, Object>> updateContactStatus(
            @PathVariable Long contactId,
            @RequestParam String status) {
        try {
            log.info("PUT /api/agents/contacts/{}/status - status: {}", contactId, status);

            if (!status.matches("UNREAD|READ|REPLIED")) {
                log.warn("Invalid status: {}", status);
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Invalid status. Must be UNREAD, READ, or REPLIED"
                        ));
            }

            AgentContactDTO contact = contactService.updateContactStatus(contactId, status);
            log.info("Contact status updated successfully");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contact status updated",
                    "data", contact
            ));
        } catch (Exception e) {
            log.error("Error updating contact status for {}", contactId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error updating status: " + e.getMessage()
                    ));
        }
    }
}