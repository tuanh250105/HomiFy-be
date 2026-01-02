package com.homifybackend.accountSetting_myListings.model;


import jakarta.persistence.*;


@Entity
@Table(name = "rental_listing_images")

public class RentalListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_listing_id", nullable = false)
    private RentalListing rentalListing;

    @Column(columnDefinition = "text", nullable = false)
    private String url;

    @Column(name = "is_primary")
    private Boolean isPrimary;
}
