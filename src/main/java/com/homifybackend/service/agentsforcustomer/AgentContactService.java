package com.homifybackend.service.agentsforcustomer;

import com.homifybackend.dto.AgentContactDTO;
import com.homifybackend.dto.CreateContactRequest;
import java.util.List;
import java.util.Optional;

public interface AgentContactService {

    AgentContactDTO createContact(Long agentId, CreateContactRequest request);

    List<AgentContactDTO> getContactsByAgentId(Long agentId);

    List<AgentContactDTO> getContactsByAgentIdAndStatus(Long agentId, String status);

    Optional<AgentContactDTO> getContactById(Long contactId);

    AgentContactDTO updateContactStatus(Long contactId, String status);

    Long getUnreadCountByAgentId(Long agentId);
}