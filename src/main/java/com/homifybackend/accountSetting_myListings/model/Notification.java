package com.homifybackend.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="notifications")

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notification_id")
    private Long notificationId;

    @Column(name="receiver_id", nullable=false)
    private Long receiverId;

    private String title;

    @Column(columnDefinition="text")
    private String message;

    @Column(name="is_read")
    private Boolean isRead;

    @Column(name="created_at")
    private Instant createdAt;
}
