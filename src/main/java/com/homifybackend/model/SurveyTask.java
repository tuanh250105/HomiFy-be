package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "survey_tasks",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_sell_request_task", columnNames = {"sell_request_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // sell_request_id UNIQUE => OneToOne hợp lý
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sell_request_id", nullable = false, unique = true)
    private SellRequest sellRequest;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @Column(name = "task_status", nullable = false)
    private String taskStatus = "CLAIMED";

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
