package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sale_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleListing {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "current_price", precision = 18, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "estimate_value", precision = 18, scale = 2)
    private BigDecimal estimateValue;

    @Column(name = "sale_status", length = 20)
    @Enumerated(EnumType.STRING)
    private SaleListingStatus saleStatus = SaleListingStatus.ACTIVE;

    @Column(name = "marketing_description", columnDefinition = "TEXT")
    private String marketingDescription;

    @CreationTimestamp
    @Column(name = "date_listed")
    private LocalDateTime dateListed;

    @OneToMany(mappedBy = "saleListing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListingImage> images = new ArrayList<>();
}