package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_outdoor_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyOutdoorFeatures {

    @Id
    @Column(name = "property_id")
    private Long propertyId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "has_swimming_pool")
    private Boolean hasSwimmingPool = false;

    @Column(name = "has_childrens_playground")
    private Boolean hasChildrensPlayground = false;
}
