package com.homifybackend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "single_houses")
@DiscriminatorValue("SINGLE_HOUSE")
@PrimaryKeyJoinColumn(name = "property_id")
public class SingleHouse extends Property {

    private Double landArea;
    private Double backyardArea;
    private Double frontYardArea;

    private Boolean hasGarage;
    private Boolean hasBasement;
}

