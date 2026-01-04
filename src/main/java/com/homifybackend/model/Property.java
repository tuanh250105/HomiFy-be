package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long propertyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Customer owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "floors")
    private Integer floors;

    @Column(name = "beds")
    private Integer beds;

    @Column(name = "baths")
    private Integer baths;

    @Column(name = "area")
    private Double area;


    @Column(name = "description")
    private String description;

    @Column(name = "property_type")
    private String propertyType;


    @OneToMany(
            mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_rating_id")
    private TransportRating transportRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appliance_rating_id")
    private ApplianceRating applianceRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 1:1 Features
    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SecurityFeatures securityFeatures;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OutdoorFeatures outdoorFeatures;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EntertainmentFeatures entertainmentFeatures;

    // 1:1 Rental Listing
    @OneToOne(mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private RentalListing rentalListing;

    @OneToOne(mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private SaleListing saleListing;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}