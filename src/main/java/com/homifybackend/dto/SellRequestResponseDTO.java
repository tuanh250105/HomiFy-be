package com.homifybackend.explore_options.dto;

import java.time.LocalDateTime;

public class SellRequestResponseDTO {
    private Long id;
    private Long ownerId;
    private Long addressId;
    private String addressText;

    private Integer estBeds;
    private Integer estBaths;
    private Integer floors;
    private Boolean hasBasement;
    private Double estimatedArea;

    // ADD: return more detail fields
    private String status;
    private LocalDateTime createdAt;

    private String neededRepairNotes;
    private String exteriorCondition;
    private String livingRoomCondition;
    private String kitchenCondition;
    private String interiorCondition;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getAddressText() { return addressText; }
    public void setAddressText(String addressText) { this.addressText = addressText; }

    public Integer getEstBeds() { return estBeds; }
    public void setEstBeds(Integer estBeds) { this.estBeds = estBeds; }

    public Integer getEstBaths() { return estBaths; }
    public void setEstBaths(Integer estBaths) { this.estBaths = estBaths; }

    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }

    public Boolean getHasBasement() { return hasBasement; }
    public void setHasBasement(Boolean hasBasement) { this.hasBasement = hasBasement; }

    public Double getEstimatedArea() { return estimatedArea; }
    public void setEstimatedArea(Double estimatedArea) { this.estimatedArea = estimatedArea; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getNeededRepairNotes() { return neededRepairNotes; }
    public void setNeededRepairNotes(String neededRepairNotes) { this.neededRepairNotes = neededRepairNotes; }

    public String getExteriorCondition() { return exteriorCondition; }
    public void setExteriorCondition(String exteriorCondition) { this.exteriorCondition = exteriorCondition; }

    public String getLivingRoomCondition() { return livingRoomCondition; }
    public void setLivingRoomCondition(String livingRoomCondition) { this.livingRoomCondition = livingRoomCondition; }

    public String getKitchenCondition() { return kitchenCondition; }
    public void setKitchenCondition(String kitchenCondition) { this.kitchenCondition = kitchenCondition; }

    public String getInteriorCondition() { return interiorCondition; }
    public void setInteriorCondition(String interiorCondition) { this.interiorCondition = interiorCondition; }
}
