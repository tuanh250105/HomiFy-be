package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "properties")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "owner_id")
    private Long ownerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "year_built")
    private Integer yearBuilt;

    private Integer floors;
    private Integer beds;
    private Integer baths;

    private Double area;

    @Column(columnDefinition = "text")
    private String description;
}
