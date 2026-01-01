package com.homifybackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    public Property() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}