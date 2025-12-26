package com.homifybackend.model;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "apartments")
@DiscriminatorValue("APARTMENT")
@PrimaryKeyJoinColumn(name = "property_id")
public class Apartments extends Property {

    private Double usableArea;

    private BigDecimal maintenanceFee;

    private Integer level;
    private Boolean hasElevatorAccess;
    private Boolean petAllowed;
    private Boolean sharedFacilities;

    private Integer totalBuildingFloors;
    private Boolean balcony;
}
