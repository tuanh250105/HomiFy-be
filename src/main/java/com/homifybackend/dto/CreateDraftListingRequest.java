package com.homifybackend.dto;

import java.util.Map;

import com.homifybackend.model.PropertyType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreateDraftListingRequest {
    
    private Long ownerId; // Optional - will be auto-assigned if null
    
    private Long agentId; // Optional - will be auto-assigned if null
    
    // Nếu có sellRequestId, sẽ lấy thông tin từ SellRequest (Survey Task)
    private Long sellRequestId;
    
    @NotNull(message = "Property type is required")
    private PropertyType propertyType;
    
    @Valid
    @NotNull(message = "Address data is required")
    private AddressData address;
    
    @Valid
    @NotNull(message = "Structure data is required")
    private StructureData structureData;
    
    private Map<String, Object> subtypeData;
    
    // Nested classes for structured data
    public static class AddressData {
        private String street;
        private String city;
        private String province;
        private String nation;
        private Double latitude;
        private Double longitude;
        
        // Getters and Setters
        public String getStreet() {
            return street;
        }
        
        public void setStreet(String street) {
            this.street = street;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getProvince() {
            return province;
        }
        
        public void setProvince(String province) {
            this.province = province;
        }
        
        public String getNation() {
            return nation;
        }
        
        public void setNation(String nation) {
            this.nation = nation;
        }
        
        public Double getLatitude() {
            return latitude;
        }
        
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
        
        public Double getLongitude() {
            return longitude;
        }
        
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
    
    public static class StructureData {
        private Integer yearBuilt;
        private Integer floors;
        private Integer beds;
        private Integer baths;
        private Double area;
        private String description;
        
        // Getters and Setters
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
    
    // Getters and Setters
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public Long getAgentId() {
        return agentId;
    }
    
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    
    public Long getSellRequestId() {
        return sellRequestId;
    }
    
    public void setSellRequestId(Long sellRequestId) {
        this.sellRequestId = sellRequestId;
    }
    
    public PropertyType getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
    
    public AddressData getAddress() {
        return address;
    }
    
    public void setAddress(AddressData address) {
        this.address = address;
    }
    
    public StructureData getStructureData() {
        return structureData;
    }
    
    public void setStructureData(StructureData structureData) {
        this.structureData = structureData;
    }
    
    public Map<String, Object> getSubtypeData() {
        return subtypeData;
    }
    
    public void setSubtypeData(Map<String, Object> subtypeData) {
        this.subtypeData = subtypeData;
    }
}
