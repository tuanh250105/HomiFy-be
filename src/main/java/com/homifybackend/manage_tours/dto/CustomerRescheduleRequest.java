package com.homifybackend.manage_tours.dto;

public class CustomerRescheduleRequest {
    private String requestedDate; // yyyy-MM-dd
    private String timeSlot;      // ex: "09:00 - 10:00" hoặc format bạn đang dùng

    public String getRequestedDate() { return requestedDate; }
    public void setRequestedDate(String requestedDate) { this.requestedDate = requestedDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
}
