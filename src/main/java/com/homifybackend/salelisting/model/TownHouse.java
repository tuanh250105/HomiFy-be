package com.homifybackend.salelisting.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "town_houses")
public class TownHouse {
    
    @Id
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "land_area")
    private Double landArea;
    
    @Column(name = "number_of_floors")
    private Integer numberOfFloors;
    
    @Column(name = "corner_lot")
    private Boolean cornerLot;
    
    @Column(name = "front_width")
    private Double frontWidth;
    
    @Column(name = "depth")
    private Double depth;
    
    @Column(name = "car_accessible")
    private Boolean carAccessible;
    
    @Column(name = "cctv_installed")
    private Boolean cctvInstalled;
    
    @Column(name = "maintenance_fee", precision = 18, scale = 2)
    private BigDecimal maintenanceFee;
    
    @Column(name = "clubhouse_access")
    private Boolean clubhouseAccess;
    
    @Column(name = "pool_access")
    private Boolean poolAccess;
    
    @Column(name = "gym_access")
    private Boolean gymAccess;
    
    @Column(name = "green_space")
    private Boolean greenSpace;
    
    @Column(name = "road_width")
    private Double roadWidth;
    
    // Constructors
    public TownHouse() {
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
    
    public Integer getNumberOfFloors() {
        return numberOfFloors;
    }
    
    public void setNumberOfFloors(Integer numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }
    
    public Boolean getCornerLot() {
        return cornerLot;
    }
    
    public void setCornerLot(Boolean cornerLot) {
        this.cornerLot = cornerLot;
    }
    
    public Double getFrontWidth() {
        return frontWidth;
    }
    
    public void setFrontWidth(Double frontWidth) {
        this.frontWidth = frontWidth;
    }
    
    public Double getDepth() {
        return depth;
    }
    
    public void setDepth(Double depth) {
        this.depth = depth;
    }
    
    public Boolean getCarAccessible() {
        return carAccessible;
    }
    
    public void setCarAccessible(Boolean carAccessible) {
        this.carAccessible = carAccessible;
    }
    
    public Boolean getCctvInstalled() {
        return cctvInstalled;
    }
    
    public void setCctvInstalled(Boolean cctvInstalled) {
        this.cctvInstalled = cctvInstalled;
    }
    
    public BigDecimal getMaintenanceFee() {
        return maintenanceFee;
    }
    
    public void setMaintenanceFee(BigDecimal maintenanceFee) {
        this.maintenanceFee = maintenanceFee;
    }
    
    public Boolean getClubhouseAccess() {
        return clubhouseAccess;
    }
    
    public void setClubhouseAccess(Boolean clubhouseAccess) {
        this.clubhouseAccess = clubhouseAccess;
    }
    
    public Boolean getPoolAccess() {
        return poolAccess;
    }
    
    public void setPoolAccess(Boolean poolAccess) {
        this.poolAccess = poolAccess;
    }
    
    public Boolean getGymAccess() {
        return gymAccess;
    }
    
    public void setGymAccess(Boolean gymAccess) {
        this.gymAccess = gymAccess;
    }
    
    public Boolean getGreenSpace() {
        return greenSpace;
    }
    
    public void setGreenSpace(Boolean greenSpace) {
        this.greenSpace = greenSpace;
    }
    
    public Double getRoadWidth() {
        return roadWidth;
    }
    
    public void setRoadWidth(Double roadWidth) {
        this.roadWidth = roadWidth;
    }
}
