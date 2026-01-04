package com.homifybackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ZestimateRequest {
    // Basic Info (6)
    @NotNull
    @Min(0)
    private Double usableArea;
    
    @NotNull
    @Min(0)
    private Double totalArea;
    
    @NotNull
    @Min(0)
    private Integer bedrooms;
    
    @NotNull
    @Min(0)
    private Integer bathrooms;
    
    @NotNull
    @Min(0)
    private Integer floors;
    
    @NotNull
    private Integer yearBuilt;
    
    // Property Type (1)
    @NotNull
    private String propertyType;
    
    // Direction (1)
    private String direction;
    
    // Structure (10)
    private Double landArea;
    private Double frontWidth;
    private Double roadWidth;
    private Double depth;
    private Double frontyardArea;
    private Double backyardArea;
    private Double lotArea;
    private Double gardenArea;
    private Integer parkingSpaces;
    private Integer floor;
    
    // Entertainment (4)
    private Boolean hasPool;
    private Boolean hasGym;
    private Boolean hasHomeTheater;
    private Boolean hasGameRoom;
    
    // Security (4)
    private Boolean hasSecurityCamera;
    private Boolean has24hSecurity;
    private Boolean hasSmartLock;
    private Boolean hasSecurityDoor;
    
    // Outdoor (3)
    private Boolean hasGarden;
    private Boolean hasPlayground;
    private Boolean hasRooftop;
    
    // Appliances (6)
    private Boolean hasDishwasher;
    private Boolean hasDryer;
    private Boolean hasMicrowave;
    private Boolean hasOven;
    private Boolean hasRefrigerator;
    private Boolean hasWasher;
    
    // Ratings (5)
    private Integer walkScore;
    private Integer bikeScore;
    private Integer transitScore;
    private Integer quietness;
    private Integer securityLevel;
    
    // Calculated (2)
    private Integer totalRooms;
    private Integer propertyAge;

    // Constructors
    public ZestimateRequest() {
        // Default values for booleans
        this.hasPool = false;
        this.hasGym = false;
        this.hasHomeTheater = false;
        this.hasGameRoom = false;
        this.hasSecurityCamera = false;
        this.has24hSecurity = false;
        this.hasSmartLock = false;
        this.hasSecurityDoor = false;
        this.hasGarden = false;
        this.hasPlayground = false;
        this.hasRooftop = false;
        this.hasDishwasher = false;
        this.hasDryer = false;
        this.hasMicrowave = false;
        this.hasOven = false;
        this.hasRefrigerator = false;
        this.hasWasher = false;
    }

    // Getters and Setters
    public Double getUsableArea() {
        return usableArea;
    }

    public void setUsableArea(Double usableArea) {
        this.usableArea = usableArea;
    }

    public Double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getFloors() {
        return floors;
    }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getLandArea() {
        return landArea;
    }

    public void setLandArea(Double landArea) {
        this.landArea = landArea;
    }

    public Double getFrontWidth() {
        return frontWidth;
    }

    public void setFrontWidth(Double frontWidth) {
        this.frontWidth = frontWidth;
    }

    public Double getRoadWidth() {
        return roadWidth;
    }

    public void setRoadWidth(Double roadWidth) {
        this.roadWidth = roadWidth;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getFrontyardArea() {
        return frontyardArea;
    }

    public void setFrontyardArea(Double frontyardArea) {
        this.frontyardArea = frontyardArea;
    }

    public Double getBackyardArea() {
        return backyardArea;
    }

    public void setBackyardArea(Double backyardArea) {
        this.backyardArea = backyardArea;
    }

    public Double getLotArea() {
        return lotArea;
    }

    public void setLotArea(Double lotArea) {
        this.lotArea = lotArea;
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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Boolean getHasPool() {
        return hasPool;
    }

    public void setHasPool(Boolean hasPool) {
        this.hasPool = hasPool;
    }

    public Boolean getHasGym() {
        return hasGym;
    }

    public void setHasGym(Boolean hasGym) {
        this.hasGym = hasGym;
    }

    public Boolean getHasHomeTheater() {
        return hasHomeTheater;
    }

    public void setHasHomeTheater(Boolean hasHomeTheater) {
        this.hasHomeTheater = hasHomeTheater;
    }

    public Boolean getHasGameRoom() {
        return hasGameRoom;
    }

    public void setHasGameRoom(Boolean hasGameRoom) {
        this.hasGameRoom = hasGameRoom;
    }

    public Boolean getHasSecurityCamera() {
        return hasSecurityCamera;
    }

    public void setHasSecurityCamera(Boolean hasSecurityCamera) {
        this.hasSecurityCamera = hasSecurityCamera;
    }

    public Boolean getHas24hSecurity() {
        return has24hSecurity;
    }

    public void setHas24hSecurity(Boolean has24hSecurity) {
        this.has24hSecurity = has24hSecurity;
    }

    public Boolean getHasSmartLock() {
        return hasSmartLock;
    }

    public void setHasSmartLock(Boolean hasSmartLock) {
        this.hasSmartLock = hasSmartLock;
    }

    public Boolean getHasSecurityDoor() {
        return hasSecurityDoor;
    }

    public void setHasSecurityDoor(Boolean hasSecurityDoor) {
        this.hasSecurityDoor = hasSecurityDoor;
    }

    public Boolean getHasGarden() {
        return hasGarden;
    }

    public void setHasGarden(Boolean hasGarden) {
        this.hasGarden = hasGarden;
    }

    public Boolean getHasPlayground() {
        return hasPlayground;
    }

    public void setHasPlayground(Boolean hasPlayground) {
        this.hasPlayground = hasPlayground;
    }

    public Boolean getHasRooftop() {
        return hasRooftop;
    }

    public void setHasRooftop(Boolean hasRooftop) {
        this.hasRooftop = hasRooftop;
    }

    public Boolean getHasDishwasher() {
        return hasDishwasher;
    }

    public void setHasDishwasher(Boolean hasDishwasher) {
        this.hasDishwasher = hasDishwasher;
    }

    public Boolean getHasDryer() {
        return hasDryer;
    }

    public void setHasDryer(Boolean hasDryer) {
        this.hasDryer = hasDryer;
    }

    public Boolean getHasMicrowave() {
        return hasMicrowave;
    }

    public void setHasMicrowave(Boolean hasMicrowave) {
        this.hasMicrowave = hasMicrowave;
    }

    public Boolean getHasOven() {
        return hasOven;
    }

    public void setHasOven(Boolean hasOven) {
        this.hasOven = hasOven;
    }

    public Boolean getHasRefrigerator() {
        return hasRefrigerator;
    }

    public void setHasRefrigerator(Boolean hasRefrigerator) {
        this.hasRefrigerator = hasRefrigerator;
    }

    public Boolean getHasWasher() {
        return hasWasher;
    }

    public void setHasWasher(Boolean hasWasher) {
        this.hasWasher = hasWasher;
    }

    public Integer getWalkScore() {
        return walkScore;
    }

    public void setWalkScore(Integer walkScore) {
        this.walkScore = walkScore;
    }

    public Integer getBikeScore() {
        return bikeScore;
    }

    public void setBikeScore(Integer bikeScore) {
        this.bikeScore = bikeScore;
    }

    public Integer getTransitScore() {
        return transitScore;
    }

    public void setTransitScore(Integer transitScore) {
        this.transitScore = transitScore;
    }

    public Integer getQuietness() {
        return quietness;
    }

    public void setQuietness(Integer quietness) {
        this.quietness = quietness;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getPropertyAge() {
        return propertyAge;
    }

    public void setPropertyAge(Integer propertyAge) {
        this.propertyAge = propertyAge;
    }
}
