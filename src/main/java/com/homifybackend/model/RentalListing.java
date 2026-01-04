package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "rental_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalListing {

    @Id
    @Column(name = "property_id")
    private Long propertyId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal monthlyRent;

    @Column(precision = 18, scale = 2)
    private BigDecimal depositAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal maintenanceFee;


    @Column(name = "available_from")
    private LocalDate availableFrom;

    @Column(name = "lease_term_months")
    private Integer leaseTermMonths;

    @Column(name = "pet_allowed")
    private Boolean petAllowed = false;

    @Column(name = "utilities_included")
    private String utilitiesIncluded;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", length = 20)
    private RentalListingStatus rentalStatus = RentalListingStatus.ACTIVE;

    @Column(name = "marketing_description", columnDefinition = "TEXT")
    private String marketingDescription;

    @Column(name = "date_listed")
    private LocalDateTime dateListed;

    @Column(name = "date_rented")
    private LocalDateTime dateRented;

    @OneToMany(mappedBy = "rentalListing",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RentalListingImage> images = new ArrayList<>();
}
