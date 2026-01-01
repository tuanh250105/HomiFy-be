package com.homifybackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sale_listings")
public class SaleListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    public SaleListing() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
}