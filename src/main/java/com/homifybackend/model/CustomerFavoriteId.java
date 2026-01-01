package com.homifybackend.model;

import java.io.Serializable;
import java.util.Objects;

public class CustomerFavoriteId implements Serializable {
    private Long customerId;
    private Long propertyId;

    public CustomerFavoriteId() {}

    public CustomerFavoriteId(Long customerId, Long propertyId) {
        this.customerId = customerId;
        this.propertyId = propertyId;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerFavoriteId)) return false;
        CustomerFavoriteId that = (CustomerFavoriteId) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(propertyId, that.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, propertyId);
    }
}
