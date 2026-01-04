package com.homifybackend.dto;

import java.util.List;

public class TourDTO {
    private Long listingId;
    private String buyerName;
    private String buyerPhone;
    private List<PreferredTimeDTO> preferredTimes;
    private String message;

    public TourDTO() {}

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public List<PreferredTimeDTO> getPreferredTimes() { return preferredTimes; }
    public void setPreferredTimes(List<PreferredTimeDTO> preferredTimes) { this.preferredTimes = preferredTimes; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}