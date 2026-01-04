package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Agent extends User {

  @Column(name = "license_id", length = 50)
  private String licenseId;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @Column(name = "rate", precision = 3, scale = 2)
  private BigDecimal rate;
}