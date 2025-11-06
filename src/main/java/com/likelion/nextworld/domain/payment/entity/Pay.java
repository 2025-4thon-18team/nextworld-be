package com.likelion.nextworld.domain.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pay {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long payId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payer_id", nullable = false)
  private User payer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  @Column(nullable = false)
  private Long amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType transactionType; // CHARGE/USE/REFUND

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PayStatus payStatus; // PENDING/COMPLETED/FAILED

  @Column(unique = true)
  private String impUid;

  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) createdAt = LocalDateTime.now();
  }
}
