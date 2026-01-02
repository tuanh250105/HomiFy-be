package com.homifybackend.agentsforcustomer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentContactDTO {

    private Long contactId;
    private Long agentId;

    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String message;

    private Long propertyId;
    private String listingType;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;
}