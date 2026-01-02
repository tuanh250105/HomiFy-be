package com.homifybackend.salelisting.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "apartments")
public class Apartment {
    
    @Id
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "usable_area")
    private Double usableArea;
    
    @Column(name = "maintenance_fee", precision = 18, scale = 2)
    private BigDecimal maintenanceFee;
    
    @Column(name = "level")
    private Integer level;
    
    @Column(name = "has_elevator_access")
    private Boolean hasElevatorAccess;
    
    @Column(name = "pet_allowed")
    private Boolean petAllowed;
    
    @Column(name = "shared_facilities")
    private Boolean sharedFacilities;
    
    @Column(name = "total_building_floors")
    private Integer totalBuildingFloors;
    
    @Column(name = "balcony")
    private Boolean balcony;
    
    // Constructors
    public Apartment() {
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public Double getUsableArea() {
        return usableArea;
    }
    
    public void setUsableArea(Double usableArea) {
        this.usableArea = usableArea;
    }
    
    public BigDecimal getMaintenanceFee() {
        return maintenanceFee;
    }
    
    public void setMaintenanceFee(BigDecimal maintenanceFee) {
        this.maintenanceFee = maintenanceFee;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public Boolean getHasElevatorAccess() {
        return hasElevatorAccess;
    }
    
    public void setHasElevatorAccess(Boolean hasElevatorAccess) {
        this.hasElevatorAccess = hasElevatorAccess;
    }
    
    public Boolean getPetAllowed() {
        return petAllowed;
    }
    
    public void setPetAllowed(Boolean petAllowed) {
        this.petAllowed = petAllowed;
    }
    
    public Boolean getSharedFacilities() {
        return sharedFacilities;
    }
    
    public void setSharedFacilities(Boolean sharedFacilities) {
        this.sharedFacilities = sharedFacilities;
    }
    
    public Integer getTotalBuildingFloors() {
        return totalBuildingFloors;
    }
    
    public void setTotalBuildingFloors(Integer totalBuildingFloors) {
        this.totalBuildingFloors = totalBuildingFloors;
    }
    
    public Boolean getBalcony() {
        return balcony;
    }
    
    public void setBalcony(Boolean balcony) {
        this.balcony = balcony;
    }
}
