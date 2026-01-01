package com.homifybackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sell_requests")
public class SellRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> customers.user_id
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // FK -> addresses.address_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "est_beds")
    private Integer estBeds;

    @Column(name = "est_baths")
    private Integer estBaths;

    @Column(name = "floors")
    private Integer floors;

    @Column(name = "has_garage")
    private Boolean hasGarage = false;

    @Column(name = "has_basement")
    private Boolean hasBasement = false;

    @Column(name = "living_room_condition", length = 50)
    private String livingRoomCondition;

    @Column(name = "kitchen_condition", length = 50)
    private String kitchenCondition;

    @Column(name = "interior_condition", length = 50)
    private String interiorCondition;

    @Column(name = "exterior_condition", length = 50)
    private String exteriorCondition;

    @Column(name = "estimated_area")
    private Double estimatedArea;

    @Column(name = "needed_repair_notes", columnDefinition = "text")
    private String neededRepairNotes;

    @Column(name = "status", length = 20)
    private String status = "PENDING";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (hasGarage == null) hasGarage = false;
        if (hasBasement == null) hasBasement = false;
    }

    // getters/setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Integer getEstBeds() { return estBeds; }
    public void setEstBeds(Integer estBeds) { this.estBeds = estBeds; }

    public Integer getEstBaths() { return estBaths; }
    public void setEstBaths(Integer estBaths) { this.estBaths = estBaths; }

    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }

    public Boolean getHasGarage() { return hasGarage; }
    public void setHasGarage(Boolean hasGarage) { this.hasGarage = hasGarage; }

    public Boolean getHasBasement() { return hasBasement; }
    public void setHasBasement(Boolean hasBasement) { this.hasBasement = hasBasement; }

    public String getLivingRoomCondition() { return livingRoomCondition; }
    public void setLivingRoomCondition(String livingRoomCondition) { this.livingRoomCondition = livingRoomCondition; }

    public String getKitchenCondition() { return kitchenCondition; }
    public void setKitchenCondition(String kitchenCondition) { this.kitchenCondition = kitchenCondition; }

    public String getInteriorCondition() { return interiorCondition; }
    public void setInteriorCondition(String interiorCondition) { this.interiorCondition = interiorCondition; }

    public String getExteriorCondition() { return exteriorCondition; }
    public void setExteriorCondition(String exteriorCondition) { this.exteriorCondition = exteriorCondition; }

    public Double getEstimatedArea() { return estimatedArea; }
    public void setEstimatedArea(Double estimatedArea) { this.estimatedArea = estimatedArea; }

    public String getNeededRepairNotes() { return neededRepairNotes; }
    public void setNeededRepairNotes(String neededRepairNotes) { this.neededRepairNotes = neededRepairNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
