package com.homifybackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {

    private Long agentId;
    private String licenseId;
    private String bio;
    private BigDecimal rate;

    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    private String email;

    private String street;
    private String city;
    private String province;
    private String zipCode;
    private String nation;
    private String fullAddress;

    private Integer reviewCount;
    private Double averageRating;
    private Integer saleListingsCount;
    private Integer rentalListingsCount;

    private String[] specialties;
}