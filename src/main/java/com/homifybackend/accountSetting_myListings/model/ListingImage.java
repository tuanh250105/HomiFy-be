package com.homifybackend.accountSetting_myListings.model;


import jakarta.persistence.*;


@Entity
@Table(name = "listing_images")

public class ListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private SaleListing listing;

    @Column(columnDefinition = "text", nullable = false)
    private String url;

    @Column(name = "is_primary")
    private Boolean isPrimary;
}
