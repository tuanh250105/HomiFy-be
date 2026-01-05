package com.homifybackend.service.agentsforcustomer;

import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.model.Agent;
import com.homifybackend.model.User;
import com.homifybackend.model.AgentReview;
import com.homifybackend.dto.AgentReviewDTO;
import com.homifybackend.dto.CreateReviewRequest;
import com.homifybackend.repository.AgentRepository;
import com.homifybackend.repository.AgentReviewRepository;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.exception.AgentNotFoundException;
import com.homifybackend.exception.ReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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

        // Validate Agent exists
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(agentId));

        Long reviewerId = request.getReviewerId();

        // Validate User exists
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reviewerId));

        // Validate that reviewer is a Customer
        if (!customerRepository.existsByUserId(reviewerId)) {
            log.error("User ID {} is not a Customer and cannot write reviews", reviewerId);
            throw new IllegalArgumentException(
                    "Only registered customers can write reviews. User ID " + reviewerId +
                            " is not a customer. Please register as a customer first."
            );
        }

        log.debug("Reviewer ID {} is a valid Customer", reviewerId);

        // Create the review
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

        try {
            // FIXED: Use native query to avoid Customer.is_favorite
            List<Object[]> results = reviewRepository.findByAgentIdNative(agentId);
            return results.stream()
                    .map(this::mapNativeResultToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching reviews for agent {}: {}", agentId, e.getMessage());
            throw new RuntimeException("Error fetching reviews: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentReviewDTO> getApprovedReviewsByAgentId(Long agentId) {
        log.debug("Fetching approved reviews for agent ID: {}", agentId);

        try {
            // FIXED: Use native query to avoid Customer.is_favorite
            List<Object[]> results = reviewRepository.findByAgentIdAndStatusNative(agentId, "APPROVED");
            return results.stream()
                    .map(this::mapNativeResultToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching approved reviews for agent {}: {}", agentId, e.getMessage());
            throw new RuntimeException("Error fetching approved reviews: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentReviewDTO> getReviewById(Long reviewId) {
        log.debug("Fetching review by ID: {}", reviewId);

        try {
            return reviewRepository.findById(reviewId)
                    .map(this::mapToDTO);
        } catch (Exception e) {
            log.error("Error fetching review {}: {}", reviewId, e.getMessage());
            return Optional.empty();
        }
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
        try {
            Double avg = reviewRepository.findAverageRatingByAgentId(agentId);
            return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
        } catch (Exception e) {
            log.error("Error calculating average rating for agent {}: {}", agentId, e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReviewCountByAgentId(Long agentId) {
        try {
            Long count = reviewRepository.countByAgent_UserIdAndStatus(agentId, "APPROVED");
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Error counting reviews for agent {}: {}", agentId, e.getMessage());
            return 0L;
        }
    }

    // FIXED: New mapper for native query results (avoids lazy loading Customer)
    private AgentReviewDTO mapNativeResultToDTO(Object[] row) {
        try {
            AgentReviewDTO.AgentReviewDTOBuilder builder = AgentReviewDTO.builder();

            // Map from Object[] (native query result)
            int i = 0;
            builder.reviewId(row[i++] != null ? ((Number) row[i - 1]).longValue() : null);
            builder.agentId(row[i++] != null ? ((Number) row[i - 1]).longValue() : null);
            builder.reviewerId(row[i++] != null ? ((Number) row[i - 1]).longValue() : null);
            builder.rating(row[i++] != null ? ((Number) row[i - 1]).intValue() : null);
            builder.title(row[i++] != null ? row[i - 1].toString() : null);
            builder.content(row[i++] != null ? row[i - 1].toString() : null);
            builder.responsiveness(row[i++] != null ? ((Number) row[i - 1]).intValue() : null);
            builder.localKnowledge(row[i++] != null ? ((Number) row[i - 1]).intValue() : null);
            builder.negotiationSkills(row[i++] != null ? ((Number) row[i - 1]).intValue() : null);
            builder.professionalism(row[i++] != null ? ((Number) row[i - 1]).intValue() : null);

            // Handle array type for imageUrls
            if (row[i] != null) {
                if (row[i] instanceof String[]) {
                    builder.imageUrls((String[]) row[i]);
                } else if (row[i].getClass().isArray()) {
                    Object[] arr = (Object[]) row[i];
                    String[] strArr = new String[arr.length];
                    for (int j = 0; j < arr.length; j++) {
                        strArr[j] = arr[j] != null ? arr[j].toString() : null;
                    }
                    builder.imageUrls(strArr);
                }
            }
            i++;

            builder.status(row[i++] != null ? row[i - 1].toString() : null);

            // Handle timestamp/LocalDateTime
            if (row[i] != null) {
                if (row[i] instanceof Timestamp) {
                    builder.reviewDate(((Timestamp) row[i]).toLocalDateTime());
                } else if (row[i] instanceof LocalDateTime) {
                    builder.reviewDate((LocalDateTime) row[i]);
                }
            }
            i++;

            if (row[i] != null) {
                if (row[i] instanceof Timestamp) {
                    builder.approvedAt(((Timestamp) row[i]).toLocalDateTime());
                } else if (row[i] instanceof LocalDateTime) {
                    builder.approvedAt((LocalDateTime) row[i]);
                }
            }
            i++;

            // Reviewer info from JOIN
            builder.reviewerName(row[i++] != null ? row[i - 1].toString() : null);
            builder.reviewerAvatar(row[i++] != null ? row[i - 1].toString() : null);

            return builder.build();
        } catch (Exception e) {
            log.error("Error mapping native result to DTO: {}", e.getMessage());
            throw new RuntimeException("Error mapping review data", e);
        }
    }

    // Original mapper for JPA entity (used in create/approve/reject)
    private AgentReviewDTO mapToDTO(AgentReview review) {
        try {
            AgentReviewDTO.AgentReviewDTOBuilder builder = AgentReviewDTO.builder()
                    .reviewId(review.getReviewId())
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

            // Safe agent ID mapping
            if (review.getAgent() != null) {
                builder.agentId(review.getAgent().getUserId());
            }

            // Safe reviewer mapping - AVOID lazy loading Customer fields
            if (review.getReviewer() != null) {
                try {
                    builder.reviewerId(review.getReviewer().getUserId());
                    // Try to get basic User fields - if fails, skip
                    try {
                        builder.reviewerName(review.getReviewer().getFullName());
                        builder.reviewerAvatar(review.getReviewer().getAvatarUrl());
                    } catch (Exception ex) {
                        log.warn("Could not load reviewer details, skipping: {}", ex.getMessage());
                    }
                } catch (Exception ex) {
                    log.warn("Could not load reviewer, skipping: {}", ex.getMessage());
                }
            }

            return builder.build();
        } catch (Exception e) {
            log.error("Error mapping review to DTO: {}", e.getMessage());
            throw new RuntimeException("Error mapping review data", e);
        }
    }
}