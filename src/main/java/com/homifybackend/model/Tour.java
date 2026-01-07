package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "tour_requests")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_date")
    private String date;

    @Column(name = "time_slot")
    private String time;

    private String status;

    @Column(name = "reschedule_count")
    private Integer rescheduleCount = 0;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    // MÌNH THÊM TRƯỜNG NÀY ĐỂ QUERY ĐƯỢC THEO AGENT
    @ManyToOne
    @JoinColumn(name = "sale_listing_id")
    private SaleListing saleListing;

    public Tour() {}
}