package com.homifybackend.dto.dashboard;

import java.util.List;

public record Performance7dDto(
    List<String> labels,
    List<Integer> newListingsSeries,
    List<Integer> viewsSeries
) {}