package com.homifybackend.model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "tour_requests")
public class TourRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> customers(user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Customer requester;

    // FK -> sale_listings(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_listing_id", nullable = false)
    private SaleListing saleListing;

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "time_slot", length = 50)
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TourRequestStatus status = TourRequestStatus.PENDING;
}
