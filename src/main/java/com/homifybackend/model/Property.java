package com.homifybackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "address_id")
    private Long addressId;

    private Integer beds;
    private Integer baths;

    @Column(name = "area")
    private Double area;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Property() {}

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public Integer getBeds() { return beds; }
    public void setBeds(Integer beds) { this.beds = beds; }

    public Integer getBaths() { return baths; }
    public void setBaths(Integer baths) { this.baths = baths; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
