package com.homifybackend.dto;

public class PropertyDTO {
    
    private Long propertyId;
    private Integer yearBuilt;
    private Integer floors;
    private Integer beds;
    private Integer baths;
    private Double area;
    private String description;
    
    // Constructors
    public PropertyDTO() {
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
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
}
