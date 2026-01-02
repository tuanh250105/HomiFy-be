package com.homifybackend.agentsforcustomer.service;

import com.homifybackend.agentsforcustomer.dto.AgentReviewDTO;
import com.homifybackend.agentsforcustomer.dto.CreateReviewRequest;
import java.util.List;
import java.util.Optional;

public interface AgentReviewService {

    AgentReviewDTO createReview(Long agentId, CreateReviewRequest request);

    List<AgentReviewDTO> getReviewsByAgentId(Long agentId);

    List<AgentReviewDTO> getApprovedReviewsByAgentId(Long agentId);

    Optional<AgentReviewDTO> getReviewById(Long reviewId);

    AgentReviewDTO approveReview(Long reviewId, Long approvedBy);

    AgentReviewDTO rejectReview(Long reviewId, Long approvedBy);

    Double getAverageRating(Long agentId);

    Long getReviewCountByAgentId(Long agentId);
}