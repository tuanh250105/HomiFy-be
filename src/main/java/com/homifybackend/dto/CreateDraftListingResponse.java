package com.homifybackend.dto;

import com.homifybackend.model.SaleStatus;

public class CreateDraftListingResponse {
    
    private Long propertyId;
    private Long listingId;
    private SaleStatus saleStatus;
    
    // Constructors
    public CreateDraftListingResponse() {
    }
    
    public CreateDraftListingResponse(Long propertyId, Long listingId, SaleStatus saleStatus) {
        this.propertyId = propertyId;
        this.listingId = listingId;
        this.saleStatus = saleStatus;
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public Long getListingId() {
        return listingId;
    }
    
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }
    
    public SaleStatus getSaleStatus() {
        return saleStatus;
    }
    
    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }
}
