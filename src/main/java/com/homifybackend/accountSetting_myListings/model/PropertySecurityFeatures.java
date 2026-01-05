package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_security_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertySecurityFeatures {

    @Id
    @Column(name = "property_id")
    private Long propertyId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "has_security_door")
    private Boolean hasSecurityDoor = false;

    @Column(name = "has_cctv")
    private Boolean hasCctv = false;
}
