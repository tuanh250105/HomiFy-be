package com.homifybackend.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TodayDto(
    @JsonProperty("tours") List<TodayTaskDto> todayTours,
    @JsonProperty("surveys") List<TodayTaskDto> todaySurveys
) {}
