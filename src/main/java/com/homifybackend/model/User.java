package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data // Tốt nhất nên dùng @Getter @Setter để an toàn hơn, nhưng @Data vẫn chạy được ở đây
@NoArgsConstructor
@AllArgsConstructor
// 👇 DÒNG NÀY CỰC KỲ QUAN TRỌNG:
// Nó bảo JPA rằng: "Dữ liệu của con (Customer) sẽ nằm ở bảng riêng, nối với bảng cha (User) qua ID"
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
  private String gender;

  @Column(name = "avatar_url", columnDefinition = "TEXT")
  private String avatarUrl;

  @Column(length = 20)
  private String role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id")
  private Address address;

  @PrePersist
  protected void onCreate() {
    if (registrationDate == null) {
      registrationDate = LocalDate.now();
    }
  }
}