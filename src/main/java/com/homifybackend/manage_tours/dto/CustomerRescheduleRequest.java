package com.homifybackend.manage_tours.dto;

public class CustomerRescheduleRequest {
    private String requestedDate; // yyyy-MM-dd
    private String timeSlot;

    public String getRequestedDate() { return requestedDate; }
    public String getTimeSlot() { return timeSlot; }

    public void setRequestedDate(String requestedDate) { this.requestedDate = requestedDate; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
}
