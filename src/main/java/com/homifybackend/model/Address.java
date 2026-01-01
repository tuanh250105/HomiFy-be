package com.homifybackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses") // 🔥 QUAN TRỌNG
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 🔥 QUAN TRỌNG
    @Column(name = "address_id")
    private Long addressId;

    private String city;

    private String street;
    private String province;
    private String nation;
    private String zipCode;

    // ===== GETTER / SETTER =====

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
