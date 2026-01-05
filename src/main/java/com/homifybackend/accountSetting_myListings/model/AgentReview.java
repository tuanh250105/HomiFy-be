package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", referencedColumnName = "user_id", nullable = false)
    private Agent agent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", referencedColumnName = "user_id", nullable = false)
    private User reviewer;
    
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "title", length = 200)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "responsiveness")
    private Integer responsiveness;
    
    @Column(name = "local_knowledge")
    private Integer localKnowledge;
    
    @Column(name = "negotiation_skills")
    private Integer negotiationSkills;
    
    @Column(name = "professionalism")
    private Integer professionalism;
    
    @Column(name = "image_urls", columnDefinition = "TEXT[]")
    private String[] imageUrls;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING";
    
    @Column(name = "review_date")
    private LocalDateTime reviewDate;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", referencedColumnName = "user_id")
    private User approvedBy;
    
    @PrePersist
    protected void onCreate() {
        if (reviewDate == null) {
            reviewDate = LocalDateTime.now();
        }
    }
}
