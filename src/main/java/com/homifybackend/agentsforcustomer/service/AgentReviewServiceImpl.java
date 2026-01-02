package com.homifybackend.agentsforcustomer.service;

import com.homifybackend.agentsforcustomer.dto.AgentReviewDTO;
import com.homifybackend.agentsforcustomer.dto.CreateReviewRequest;
import com.homifybackend.agentsforcustomer.repository.AgentRepository;
import com.homifybackend.agentsforcustomer.repository.AgentReviewRepository;
import com.homifybackend.agentsforcustomer.repository.CustomerRepository;
import com.homifybackend.agentsforcustomer.repository.UserRepository;
import com.homifybackend.agentsforcustomer.exception.AgentNotFoundException;
import com.homifybackend.agentsforcustomer.exception.ReviewNotFoundException;
import com.homifybackend.model.Agent;
import com.homifybackend.model.AgentReview;
import com.homifybackend.model.Customer;
import com.homifybackend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentReviewServiceImpl implements AgentReviewService {

    private final AgentReviewRepository reviewRepository;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AgentReviewDTO createReview(Long agentId, CreateReviewRequest request) {
        log.info("Creating review for agent ID: {} by reviewer ID: {}", agentId, request.getReviewerId());

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(agentId));

        Long reviewerId = request.getReviewerId();

        // Fetch User from database
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reviewerId));

        // Ensure reviewer is a customer
        if (!customerRepository.existsByUserId(reviewerId)) {
            log.debug("Creating customer record for user ID: {}", reviewerId);
            Customer newCustomer = new Customer();
            // ✅ FIXED: Only set user - userId auto-set via @MapsId
            newCustomer.setUser(reviewer);
            customerRepository.save(newCustomer);
        }

        AgentReview review = new AgentReview();
        review.setAgent(agent);
        review.setReviewer(reviewer);

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setResponsiveness(request.getResponsiveness());
        review.setLocalKnowledge(request.getLocalKnowledge());
        review.setNegotiationSkills(request.getNegotiationSkills());
        review.setProfessionalism(request.getProfessionalism());
        review.setImageUrls(request.getImageUrls());
        review.setStatus("PENDING");

        AgentReview saved = reviewRepository.save(review);
        log.info("Review created successfully with ID: {}", saved.getReviewId());

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentReviewDTO> getReviewsByAgentId(Long agentId) {
        log.debug("Fetching all reviews for agent ID: {}", agentId);
        return reviewRepository.findByAgent_UserIdOrderByReviewDateDesc(agentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentReviewDTO> getApprovedReviewsByAgentId(Long agentId) {
        log.debug("Fetching approved reviews for agent ID: {}", agentId);
        return reviewRepository.findByAgent_UserIdAndStatusOrderByReviewDateDesc(agentId, "APPROVED")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentReviewDTO> getReviewById(Long reviewId) {
        log.debug("Fetching review by ID: {}", reviewId);
        return reviewRepository.findById(reviewId)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public AgentReviewDTO approveReview(Long reviewId, Long approvedBy) {
        log.info("Approving review ID: {} by user ID: {}", reviewId, approvedBy);

        AgentReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.setStatus("APPROVED");
        review.setApprovedAt(LocalDateTime.now());

        if (approvedBy != null) {
            User approver = userRepository.findById(approvedBy)
                    .orElseThrow(() -> new RuntimeException("Approver not found with ID: " + approvedBy));
            review.setApprovedBy(approver);
        }

        AgentReview updated = reviewRepository.save(review);
        log.info("Review approved successfully");

        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public AgentReviewDTO rejectReview(Long reviewId, Long approvedBy) {
        log.info("Rejecting review ID: {} by user ID: {}", reviewId, approvedBy);

        AgentReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.setStatus("REJECTED");
        review.setApprovedAt(LocalDateTime.now());

        if (approvedBy != null) {
            User approver = userRepository.findById(approvedBy)
                    .orElseThrow(() -> new RuntimeException("Approver not found with ID: " + approvedBy));
            review.setApprovedBy(approver);
        }

        AgentReview updated = reviewRepository.save(review);
        log.info("Review rejected successfully");

        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long agentId) {
        Double avg = reviewRepository.findAverageRatingByAgentId(agentId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReviewCountByAgentId(Long agentId) {
        Long count = reviewRepository.countByAgent_UserIdAndStatus(agentId, "APPROVED");
        return count != null ? count : 0L;
    }

    private AgentReviewDTO mapToDTO(AgentReview review) {
        AgentReviewDTO.AgentReviewDTOBuilder builder = AgentReviewDTO.builder()
                .reviewId(review.getReviewId())
                .agentId(review.getAgent() != null ? review.getAgent().getUserId() : null)
                .reviewerId(review.getReviewer() != null ? review.getReviewer().getUserId() : null)
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .responsiveness(review.getResponsiveness())
                .localKnowledge(review.getLocalKnowledge())
                .negotiationSkills(review.getNegotiationSkills())
                .professionalism(review.getProfessionalism())
                .imageUrls(review.getImageUrls())
                .status(review.getStatus())
                .reviewDate(review.getReviewDate())
                .approvedAt(review.getApprovedAt());

        if (review.getReviewer() != null) {
            builder.reviewerName(review.getReviewer().getFullName());
            builder.reviewerAvatar(review.getReviewer().getAvatarUrl());
        }

        return builder.build();
    }
}