package com.homifybackend.model;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "town_houses")
@DiscriminatorValue("TOWN_HOUSE")
@PrimaryKeyJoinColumn(name = "property_id")
public class TownHouse extends Property {

    private Double landArea;
    private Integer numberOfFloors;
    private Boolean cornerLot;

    private Double frontWidth;
    private Double depth;

    private Boolean carAccessible;
    private Boolean cctvInstalled;

    private BigDecimal maintenanceFee;

    private Boolean clubhouseAccess;
    private Boolean poolAccess;
    private Boolean gymAccess;
    private Boolean greenSpace;

    private Double roadWidth;
}
