package com.homifybackend.salelisting.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "address_id", nullable = false)
    private Long addressId;
    
    @Column(name = "year_built")
    private Integer yearBuilt;
    
    @Column(name = "floors")
    private Integer floors;
    
    @Column(name = "beds")
    private Integer beds;
    
    @Column(name = "baths")
    private Integer baths;
    
    @Column(name = "area")
    private Double area;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "transport_rating_id")
    private Long transportRatingId;
    
    @Column(name = "appliance_rating_id")
    private Long applianceRatingId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Property() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Property(Long ownerId, Long addressId, Integer yearBuilt, Integer floors, Integer beds, 
                   Integer baths, Double area, String description) {
        this.ownerId = ownerId;
        this.addressId = addressId;
        this.yearBuilt = yearBuilt;
        this.floors = floors;
        this.beds = beds;
        this.baths = baths;
        this.area = area;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public Long getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
    
    public Integer getYearBuilt() {
        return yearBuilt;
    }
    
    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }
    
    public Integer getFloors() {
        return floors;
    }
    
    public void setFloors(Integer floors) {
        this.floors = floors;
    }
    
    public Integer getBeds() {
        return beds;
    }
    
    public void setBeds(Integer beds) {
        this.beds = beds;
    }
    
    public Integer getBaths() {
        return baths;
    }
    
    public void setBaths(Integer baths) {
        this.baths = baths;
    }
    
    public Double getArea() {
        return area;
    }
    
    public void setArea(Double area) {
        this.area = area;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getTransportRatingId() {
        return transportRatingId;
    }
    
    public void setTransportRatingId(Long transportRatingId) {
        this.transportRatingId = transportRatingId;
    }
    
    public Long getApplianceRatingId() {
        return applianceRatingId;
    }
    
    public void setApplianceRatingId(Long applianceRatingId) {
        this.applianceRatingId = applianceRatingId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
