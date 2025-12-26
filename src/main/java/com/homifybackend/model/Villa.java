package com.homifybackend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "villas")
@DiscriminatorValue("VILLA")
@PrimaryKeyJoinColumn(name = "property_id")
public class Villa extends Property {

    private Double lotArea;
    private Double backyardArea;
    private Double frontYardArea;
    private Double gardenArea;

    private Integer parkingSpaces;
    private Boolean hasGarage;
    private Boolean hasBasement;

    private Double garageArea;
    private String viewType;
    private Integer smartHomeLevel;
    private Double serviceArea;
}
