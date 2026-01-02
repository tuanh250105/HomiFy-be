package com.homifybackend.salelisting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "villas")
public class Villa {
    
    @Id
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "lot_area")
    private Double lotArea;
    
    @Column(name = "backyard_area")
    private Double backyardArea;
    
    @Column(name = "front_yard_area")
    private Double frontYardArea;
    
    @Column(name = "garden_area")
    private Double gardenArea;
    
    @Column(name = "parking_spaces")
    private Integer parkingSpaces;
    
    @Column(name = "has_garage")
    private Boolean hasGarage;
    
    @Column(name = "has_basement")
    private Boolean hasBasement;
    
    @Column(name = "garage_area")
    private Double garageArea;
    
    @Column(name = "view_type")
    private String viewType;
    
    @Column(name = "smart_home_level")
    private Integer smartHomeLevel;
    
    @Column(name = "service_area")
    private Double serviceArea;
    
    // Constructors
    public Villa() {
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public Double getLotArea() {
        return lotArea;
    }
    
    public void setLotArea(Double lotArea) {
        this.lotArea = lotArea;
    }
    
    public Double getBackyardArea() {
        return backyardArea;
    }
    
    public void setBackyardArea(Double backyardArea) {
        this.backyardArea = backyardArea;
    }
    
    public Double getFrontYardArea() {
        return frontYardArea;
    }
    
    public void setFrontYardArea(Double frontYardArea) {
        this.frontYardArea = frontYardArea;
    }
    
    public Double getGardenArea() {
        return gardenArea;
    }
    
    public void setGardenArea(Double gardenArea) {
        this.gardenArea = gardenArea;
    }
    
    public Integer getParkingSpaces() {
        return parkingSpaces;
    }
    
    public void setParkingSpaces(Integer parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }
    
    public Boolean getHasGarage() {
        return hasGarage;
    }
    
    public void setHasGarage(Boolean hasGarage) {
        this.hasGarage = hasGarage;
    }
    
    public Boolean getHasBasement() {
        return hasBasement;
    }
    
    public void setHasBasement(Boolean hasBasement) {
        this.hasBasement = hasBasement;
    }
    
    public Double getGarageArea() {
        return garageArea;
    }
    
    public void setGarageArea(Double garageArea) {
        this.garageArea = garageArea;
    }
    
    public String getViewType() {
        return viewType;
    }
    
    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
    
    public Integer getSmartHomeLevel() {
        return smartHomeLevel;
    }
    
    public void setSmartHomeLevel(Integer smartHomeLevel) {
        this.smartHomeLevel = smartHomeLevel;
    }
    
    public Double getServiceArea() {
        return serviceArea;
    }
    
    public void setServiceArea(Double serviceArea) {
        this.serviceArea = serviceArea;
    }
}
