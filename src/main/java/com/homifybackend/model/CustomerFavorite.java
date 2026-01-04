package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CustomerFavoriteId.class)
public class CustomerFavorite {

  @Id
  @Column(name = "customer_id")
  private Long customerId;

  @Id
  @Column(name = "property_id")
  private Long propertyId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", insertable = false, updatable = false)
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id", insertable = false, updatable = false)
  private Property property;

  @CreationTimestamp
  @Column(name = "date_added")
  private LocalDateTime dateAdded;
}