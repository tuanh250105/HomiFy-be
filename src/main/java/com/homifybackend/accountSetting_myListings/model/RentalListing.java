package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_listings")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_id") // Đảm bảo tên "agent_id" khớp với cột trong bảng database
    private Long agentId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "monthly_rent")
    private BigDecimal monthlyRent;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "maintenance_fee")
    private BigDecimal maintenanceFee;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    @Column(name = "lease_term_months")
    private Integer leaseTermMonths;

    @Column(name = "pet_allowed")
    private Boolean petAllowed;

    @Column(name = "utilities_included")
    private String utilitiesIncluded;

    @Column(name = "listing_status")
    private String listingStatus;

    @Column(name = "marketing_description", columnDefinition = "text")
    private String marketingDescription;

    @Column(name = "date_listed")
    private LocalDateTime dateListed;
}
