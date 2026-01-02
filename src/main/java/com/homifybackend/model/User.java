package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    @Column(name = "gender", length = 20)
    private String gender;
    
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
    
    @Column(name = "role", length = 20)
    private String role;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    private Address address;
    
    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }
}
