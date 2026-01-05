package com.homifybackend.dto.survey;

import java.time.LocalDateTime;

public record ScheduleSurveyRequest(
    Long taskId,
    LocalDateTime scheduledAt
) {}
