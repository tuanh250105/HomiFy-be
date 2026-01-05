package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long notificationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  @Column(length = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String message;

  @Column(name = "is_read")
  private boolean read = false;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}