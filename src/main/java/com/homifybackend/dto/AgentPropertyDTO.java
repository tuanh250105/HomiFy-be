package com.homifybackend.dto;

public class AgentPropertyDTO {
    private Long id;
    private Double area;
    private Double price;
    private String city;
    private String street;
    private Integer beds;
    private Integer baths;
    private Integer floors;
    private String description;
    private Integer yearBuilt;
    private String propertyType;
    private String address;
    private Integer matchScore;

    public AgentPropertyDTO() {}

    public Integer getBeds() { return beds; }
    public void setBeds(Integer beds) { this.beds = beds; }

    public Integer getBaths() { return baths; }
    public void setBaths(Integer baths) { this.baths = baths; }

    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
}
