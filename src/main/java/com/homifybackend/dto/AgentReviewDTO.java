package com.homifybackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentReviewDTO {

    private Long reviewId;
    private Long agentId;
    private Long reviewerId;

    private String reviewerName;
    private String reviewerAvatar;

    private Integer rating;
    private String title;
    private String content;

    private Integer responsiveness;
    private Integer localKnowledge;
    private Integer negotiationSkills;
    private Integer professionalism;

    private String[] imageUrls;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reviewDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime approvedAt;
}