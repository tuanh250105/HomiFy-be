package com.homifybackend.salelisting.dto;

import com.homifybackend.salelisting.model.PropertyType;
import com.homifybackend.salelisting.model.SaleStatus;

import java.math.BigDecimal;
import java.util.Map;

public class ListingResponse {
    
    private Long listingId;
    private Long propertyId;
    private SaleStatus saleStatus;
    private PropertyType propertyType;
    
    // Property data
    private Map<String, Object> property;
    private Map<String, Object> address;
    private Map<String, Object> subtype;
    
    // Additional data
    private Map<String, Object> rooms;
    private Map<String, Object> features;
    private Map<String, Object> ratings;
    
    // Listing specific data
    private BigDecimal currentPrice;
    private BigDecimal estimateValue;
    private String marketingDescription;
    private Map<String, Object> images;
    
    // Constructors
    public ListingResponse() {
    }
    
    // Getters and Setters
    public Long getListingId() {
        return listingId;
    }
    
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }
    
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public SaleStatus getSaleStatus() {
        return saleStatus;
    }
    
    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }
    
    public PropertyType getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
    
    public Map<String, Object> getProperty() {
        return property;
    }
    
    public void setProperty(Map<String, Object> property) {
        this.property = property;
    }
    
    public Map<String, Object> getAddress() {
        return address;
    }
    
    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }
    
    public Map<String, Object> getSubtype() {
        return subtype;
    }
    
    public void setSubtype(Map<String, Object> subtype) {
        this.subtype = subtype;
    }
    
    public Map<String, Object> getRooms() {
        return rooms;
    }
    
    public void setRooms(Map<String, Object> rooms) {
        this.rooms = rooms;
    }
    
    public Map<String, Object> getFeatures() {
        return features;
    }
    
    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
    
    public Map<String, Object> getRatings() {
        return ratings;
    }
    
    public void setRatings(Map<String, Object> ratings) {
        this.ratings = ratings;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getEstimateValue() {
        return estimateValue;
    }
    
    public void setEstimateValue(BigDecimal estimateValue) {
        this.estimateValue = estimateValue;
    }
    
    public String getMarketingDescription() {
        return marketingDescription;
    }
    
    public void setMarketingDescription(String marketingDescription) {
        this.marketingDescription = marketingDescription;
    }
    
    public Map<String, Object> getImages() {
        return images;
    }
    
    public void setImages(Map<String, Object> images) {
        this.images = images;
    }
}
