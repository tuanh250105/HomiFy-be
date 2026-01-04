package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column(name = "registration_date")
  private LocalDate registrationDate;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Column(length = 20)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(name = "avatar_url", columnDefinition = "TEXT")
  private String avatarUrl;

  @Column(length = 20)
  @Enumerated(EnumType.STRING)
  private Role role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id")
  private Address address;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Account account;

  @PrePersist
  protected void onCreate() {
    if (registrationDate == null) {
      registrationDate = LocalDate.now();
    }
  }
}