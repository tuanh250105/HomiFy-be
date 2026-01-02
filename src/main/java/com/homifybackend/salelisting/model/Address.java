package com.homifybackend.salelisting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "addresses")
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;
    
    @Column(name = "street")
    private String street;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "province")
    private String province;
    
    @Column(name = "nation")
    private String nation;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    // Constructors
    public Address() {
    }
    
    public Address(String street, String city, String province, String nation, Double latitude, Double longitude) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.nation = nation;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public Long getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getNation() {
        return nation;
    }
    
    public void setNation(String nation) {
        this.nation = nation;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
