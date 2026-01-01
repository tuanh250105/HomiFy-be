package com.homifybackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_saved_searches")
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "address_text", nullable = false, length = 255)
    private String addressText;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "listing_type", nullable = false, length = 10)
    private String listingType; // BUY / RENT

    @Column(name = "subscriptions_on", nullable = false)
    private Boolean subscriptionsOn = false; // dùng Boolean để getter là getSubscriptionsOn()

    @Column(name = "frequency", nullable = false, length = 20)
    private String frequency; // INSTANT / DAILY

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (subscriptionsOn == null) subscriptionsOn = false;
        if (listingType == null) listingType = "BUY";
        if (frequency == null) frequency = "INSTANT";
        if (name == null) name = "Saved search";
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== getters/setters (KHÔNG dùng Lombok để khỏi lỗi) =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getAddressText() { return addressText; }
    public void setAddressText(String addressText) { this.addressText = addressText; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }

    public Boolean getSubscriptionsOn() { return subscriptionsOn; }
    public void setSubscriptionsOn(Boolean subscriptionsOn) { this.subscriptionsOn = subscriptionsOn; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }
    public void setLastCheckedAt(LocalDateTime lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
