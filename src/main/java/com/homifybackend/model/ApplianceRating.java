package com.homifybackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appliance_ratings")
public class ApplianceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    private Boolean dishwasher;
    private Boolean dryer;
    private Boolean microwave;
    private Boolean oven;
    private Boolean refrigerator;
    private Boolean washer;
}
