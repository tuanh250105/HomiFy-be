package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", referencedColumnName = "user_id", nullable = false)
    private Agent agent;

    @Column(name = "sender_name", length = 100, nullable = false)
    private String senderName;

    @Column(name = "sender_email", length = 100, nullable = false)
    private String senderEmail;

    @Column(name = "sender_phone", length = 20)
    private String senderPhone;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    // ✅ ADDED: Property context fields
    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "listing_type", length = 20)
    private String listingType;

    @Column(name = "status", length = 20)
    private String status = "UNREAD";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}