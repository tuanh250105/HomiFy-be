package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rental_listing_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalListingImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_listing_id", nullable = false)
  private RentalListing rentalListing;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String url;

  @Column(name = "is_primary")
  private Boolean isPrimary = false;
}