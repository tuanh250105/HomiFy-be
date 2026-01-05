package com.homifybackend.dto;

public class PreferredTimeDTO {
    private String date;
    private String time;

    public PreferredTimeDTO() {}

    public PreferredTimeDTO(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
