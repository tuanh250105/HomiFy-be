package com.homifybackend.dto.dashboard;

public record OverviewDto(
    int activeListings,
    int soldDeals,
    int avgDaysToSell,
    int newLeads,
    int todayTasks
) {}