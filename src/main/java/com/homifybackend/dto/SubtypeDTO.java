package com.homifybackend.dto;

import java.util.Map;

public class SubtypeDTO {
    
    // Dùng Map vì mỗi subtype có fields khác nhau
    // SingleHouse: hasGarage, landArea...
    // Apartment: level, hasElevator...
    // Villa: viewType, smartHomeLevel...
    private Map<String, Object> data;
    
    // Constructors
    public SubtypeDTO() {
    }
    
    public SubtypeDTO(Map<String, Object> data) {
        this.data = data;
    }
    
    // Getter and Setter
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
