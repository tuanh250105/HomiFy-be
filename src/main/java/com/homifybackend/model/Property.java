package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "property_type")
public abstract class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    private Long ownerId;

    @OneToOne
    @Column(name="addresses")
    private Address address;

    private Integer yearBuilt;
    private Integer floors;
    private Integer beds;
    private Integer baths;
    private Double area;

    @Column(columnDefinition = "text")
    private String description;

    @OneToOne
    @JoinColumn(name = "transport_rating_id")
    private TransportRating transportRating;

    @OneToOne
    @JoinColumn(name = "appliance_rating_id")
    private ApplianceRating applianceRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
