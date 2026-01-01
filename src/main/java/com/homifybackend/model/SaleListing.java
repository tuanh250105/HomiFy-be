package com.homifybackend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_listings")
public class SaleListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "date_listed")
    private LocalDateTime dateListed;

    public SaleListing() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public String getSaleStatus() { return saleStatus; }
    public void setSaleStatus(String saleStatus) { this.saleStatus = saleStatus; }

    public LocalDateTime getDateListed() { return dateListed; }
    public void setDateListed(LocalDateTime dateListed) { this.dateListed = dateListed; }
}
