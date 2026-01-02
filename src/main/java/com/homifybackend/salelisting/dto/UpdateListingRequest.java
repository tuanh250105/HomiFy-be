package com.homifybackend.salelisting.dto;

import java.util.Map;

public class UpdateListingRequest {
    
    private Map<String, Object> structureData;
    private Map<String, Object> rooms;
    private Map<String, Object> amenities;
    private Map<String, Object> ratings;
    private Map<String, Object> pricing;
    private Map<String, Object> media;
    private Map<String, Object> marketing;
    
    // Getters and Setters
    public Map<String, Object> getStructureData() {
        return structureData;
    }
    
    public void setStructureData(Map<String, Object> structureData) {
        this.structureData = structureData;
    }
    
    public Map<String, Object> getRooms() {
        return rooms;
    }
    
    public void setRooms(Map<String, Object> rooms) {
        this.rooms = rooms;
    }
    
    public Map<String, Object> getAmenities() {
        return amenities;
    }
    
    public void setAmenities(Map<String, Object> amenities) {
        this.amenities = amenities;
    }
    
    public Map<String, Object> getRatings() {
        return ratings;
    }
    
    public void setRatings(Map<String, Object> ratings) {
        this.ratings = ratings;
    }
    
    public Map<String, Object> getPricing() {
        return pricing;
    }
    
    public void setPricing(Map<String, Object> pricing) {
        this.pricing = pricing;
    }
    
    public Map<String, Object> getMedia() {
        return media;
    }
    
    public void setMedia(Map<String, Object> media) {
        this.media = media;
    }
    
    public Map<String, Object> getMarketing() {
        return marketing;
    }
    
    public void setMarketing(Map<String, Object> marketing) {
        this.marketing = marketing;
    }
}
