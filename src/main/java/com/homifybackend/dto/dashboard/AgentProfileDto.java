package com.homifybackend.dto.dashboard;

public record AgentProfileDto(
    long id,
    String name,
    String email,
    String phone,
    String avatar,
    String joinDate,
    String licenseId,
    String bio,
    Double rate,
    String address,
    int totalListings,
    int soldDeals
) {}