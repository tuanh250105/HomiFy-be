package com.homifybackend.dto;

public class PreferredTimeDTO {
    private String date; // YYYY-MM-DD
    private String time; // HH:mm AM/PM

    public PreferredTimeDTO() {}

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}