package com.homifybackend.salelisting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "single_houses")
public class SingleHouse {
    
    @Id
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "land_area")
    private Double landArea;
    
    @Column(name = "backyard_area")
    private Double backyardArea;
    
    @Column(name = "front_yard_area")
    private Double frontYardArea;
    
    @Column(name = "has_garage")
    private Boolean hasGarage;
    
    @Column(name = "has_basement")
    private Boolean hasBasement;
    
    // Constructors
    public SingleHouse() {
    }
    
    public SingleHouse(Long propertyId, Double landArea, Double backyardArea, Double frontYardArea, 
                      Boolean hasGarage, Boolean hasBasement) {
        this.propertyId = propertyId;
        this.landArea = landArea;
        this.backyardArea = backyardArea;
        this.frontYardArea = frontYardArea;
        this.hasGarage = hasGarage;
        this.hasBasement = hasBasement;
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public Double getLandArea() {
        return landArea;
    }
    
    public void setLandArea(Double landArea) {
        this.landArea = landArea;
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
}
