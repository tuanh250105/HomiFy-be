package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true, nullable = false)
    private User user;
    
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;
    
    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;
    
    @Column(name = "password", length = 255, nullable = false)
    private String password;
}
