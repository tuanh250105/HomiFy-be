package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_listings")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_id")
    private Long agentId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "estimate_value")
    private BigDecimal estimateValue;

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "marketing_description", columnDefinition = "text")
    private String marketingDescription;

    @Column(name = "date_listed")
    private LocalDateTime dateListed;
}
