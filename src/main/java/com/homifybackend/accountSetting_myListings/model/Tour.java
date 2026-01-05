package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;

@Entity
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
    private String buyer;
    private String property;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    // MÌNH THÊM TRƯỜNG NÀY ĐỂ QUERY ĐƯỢC THEO AGENT
    @ManyToOne
    @JoinColumn(name = "sale_listing_id")
    private SaleListing saleListing;

    public Tour() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getRescheduleCount() { return rescheduleCount; }
    public void setRescheduleCount(Integer rescheduleCount) { this.rescheduleCount = rescheduleCount; }

    public String getBuyer() { return buyer; }
    public void setBuyer(String buyer) { this.buyer = buyer; }

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public SaleListing getSaleListing() { return saleListing; }
    public void setSaleListing(SaleListing saleListing) { this.saleListing = saleListing; }
}