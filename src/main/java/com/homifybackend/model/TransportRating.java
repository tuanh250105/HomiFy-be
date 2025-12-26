package com.homifybackend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "transport_ratings")
public class TransportRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    private Integer walkScore;
    private Integer bikeScore;
    private Integer transitScore;
}
