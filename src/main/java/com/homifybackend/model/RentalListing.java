package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id", unique = true, nullable = false)
  private Property property;

  @Column(name = "monthly_rent", nullable = false)
  private Double monthlyRent;

  @Column(name = "deposit_amount")
  private Double depositAmount;

  @Column(name = "maintenance_fee")
  private Double maintenanceFee;

  @Column(name = "available_from")
  private LocalDate availableFrom;

  @Column(name = "lease_term_months")
  private Integer leaseTermMonths;

  @Column(name = "pet_allowed")
  private Boolean petAllowed = false;

  @Column(name = "utilities_included", length = 255)
  private String utilitiesIncluded;

  @Column(name = "listing_status", length = 20)
  private String listingStatus = "ACTIVE";

  @Column(name = "marketing_description", columnDefinition = "TEXT")
  private String marketingDescription;

  @Column(name = "date_listed")
  private LocalDateTime dateListed;

  @Column(name = "date_rented")
  private LocalDateTime dateRented;

  @OneToMany(mappedBy = "rentalListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<RentalListingImage> images = new ArrayList<>();
}