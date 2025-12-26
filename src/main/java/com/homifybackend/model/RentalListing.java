package com.homifybackend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "rental_listings")
public class RentalListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    @Column(name = "monthly_rent", nullable = false, precision = 18, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "deposit_amount", precision = 18, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "maintenance_fee", precision = 18, scale = 2)
    private BigDecimal maintenanceFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", length = 20)
    private RentalListingStatus rentalStatus = RentalListingStatus.ACTIVE;

    @Column(name = "date_listed")
    private LocalDateTime dateListed;
}

