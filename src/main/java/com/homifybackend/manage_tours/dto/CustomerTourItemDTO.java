package com.homifybackend.manage_tours.dto;

import java.time.LocalDate;

public class CustomerTourItemDTO {
    private Long id;
    private Long saleListingId;
    private LocalDate requestedDate;
    private String timeSlot;
    private String status;

    private Long agentId;
    private String agentName;

    private String addressText;

    public CustomerTourItemDTO() {}

    public CustomerTourItemDTO(Long id, Long saleListingId, LocalDate requestedDate, String timeSlot,
                               String status, Long agentId, String agentName, String addressText) {
        this.id = id;
        this.saleListingId = saleListingId;
        this.requestedDate = requestedDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.agentId = agentId;
        this.agentName = agentName;
        this.addressText = addressText;
    }

    public Long getId() { return id; }
    public Long getSaleListingId() { return saleListingId; }
    public LocalDate getRequestedDate() { return requestedDate; }
    public String getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    public Long getAgentId() { return agentId; }
    public String getAgentName() { return agentName; }
    public String getAddressText() { return addressText; }

    public void setId(Long id) { this.id = id; }
    public void setSaleListingId(Long saleListingId) { this.saleListingId = saleListingId; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public void setStatus(String status) { this.status = status; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public void setAddressText(String addressText) { this.addressText = addressText; }
}
