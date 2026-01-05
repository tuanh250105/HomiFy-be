// package com.homifybackend.model;

// import jakarta.persistence.*;
// import lombok.*;
// import org.hibernate.annotations.CreationTimestamp;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "bank_transactions")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @ToString
// public class BankTransaction {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// @Column(name = "txn_id")
// private Long txnId;

// @Column(name = "txn_date", nullable = false)
// private LocalDate txnDate;

// @Column(nullable = false, precision = 18, scale = 2)
// private BigDecimal amount;

// @Enumerated(EnumType.STRING)
// @Column(length = 10)
// private BankTransactionDirection direction = BankTransactionDirection.CREDIT;

// @Column(columnDefinition = "TEXT")
// private String description;

// @Column(length = 100)
// private String reference;

// @CreationTimestamp
// @Column(name = "created_at", updatable = false)
// private LocalDateTime createdAt;
// }