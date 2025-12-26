package com.homifybackend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sale_listings")
public class SaleListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @OneToOne
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    @Column(name = "current_price", precision = 18, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "estimate_value", precision = 18, scale = 2)
    private BigDecimal estimateValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", length = 20)
    private SaleListingStatus saleStatus = SaleListingStatus.ACTIVE;

    @Column(name = "marketing_description", columnDefinition = "text")
    private String marketingDescription;

    @Column(name = "date_listed")
    private LocalDateTime dateListed;
}
