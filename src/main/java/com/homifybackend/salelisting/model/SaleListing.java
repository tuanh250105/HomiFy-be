package com.homifybackend.salelisting.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sale_listings")
public class SaleListing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "agent_id", nullable = false)
    private Long agentId;
    
    @Column(name = "property_id", unique = true, nullable = false)
    private Long propertyId;
    
    @Column(name = "current_price", precision = 18, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "estimate_value", precision = 18, scale = 2)
    private BigDecimal estimateValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status")
    private SaleStatus saleStatus;
    
    @Column(name = "marketing_description", columnDefinition = "TEXT")
    private String marketingDescription;
    
    @Column(name = "date_listed")
    private LocalDateTime dateListed;
    
    // Constructors
    public SaleListing() {
        this.saleStatus = SaleStatus.DRAFT;
        this.dateListed = LocalDateTime.now();
    }
    
    public SaleListing(Long agentId, Long propertyId, SaleStatus saleStatus) {
        this.agentId = agentId;
        this.propertyId = propertyId;
        this.saleStatus = saleStatus;
        this.dateListed = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAgentId() {
        return agentId;
    }
    
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getEstimateValue() {
        return estimateValue;
    }
    
    public void setEstimateValue(BigDecimal estimateValue) {
        this.estimateValue = estimateValue;
    }
    
    public SaleStatus getSaleStatus() {
        return saleStatus;
    }
    
    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }
    
    public String getMarketingDescription() {
        return marketingDescription;
    }
    
    public void setMarketingDescription(String marketingDescription) {
        this.marketingDescription = marketingDescription;
    }
    
    public LocalDateTime getDateListed() {
        return dateListed;
    }
    
    public void setDateListed(LocalDateTime dateListed) {
        this.dateListed = dateListed;
    }
}
