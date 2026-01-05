package com.homifybackend.dto;

public class AgentPropertyDTO {
    private Long id;
    private Double area;
    private Double price;
    private String city;
    private String street;

    public AgentPropertyDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
}
