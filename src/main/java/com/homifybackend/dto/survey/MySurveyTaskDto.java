package com.homifybackend.dto.survey;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MySurveyTaskDto(
        @JsonProperty("task_id") Long taskId,
        @JsonProperty("sell_request_id") Long sellRequestId,

        @JsonProperty("task_status") String taskStatus,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("scheduled_at") String scheduledAt,

        @JsonProperty("address_id") Long addressId,
        @JsonProperty("est_beds") Integer estBeds,
        @JsonProperty("est_baths") Integer estBaths,
        @JsonProperty("estimated_area") Double estimatedArea,
        
        @JsonProperty("note") String note // ✅ Đã thêm trường note
) {}
