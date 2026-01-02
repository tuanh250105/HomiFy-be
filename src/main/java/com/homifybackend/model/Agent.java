package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "license_id", length = 50)
    private String licenseId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "rate", precision = 3, scale = 2)
    private BigDecimal rate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "specialties", columnDefinition = "TEXT[]")
    private String[] specialties;
}