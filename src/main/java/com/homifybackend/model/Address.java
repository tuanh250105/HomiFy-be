package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(length = 255)
    private String street;

    @Column(length = 100)
    private String nation;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
}

