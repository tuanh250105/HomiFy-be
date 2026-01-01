package com.homifybackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_favorites")
@IdClass(CustomerFavoriteId.class)
public class CustomerFavorite {
    @Id
    @Column(name="customer_id")
    private Long customerId;

    @Id
    @Column(name="property_id")
    private Long propertyId;

    @Column(name="date_added")
    private LocalDateTime dateAdded;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
}
