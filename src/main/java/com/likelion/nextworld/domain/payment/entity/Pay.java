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
@Table(name = "pay")
public class Pay {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pay_id")
  private Long payId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User payer;

  @Column(nullable = false)
  private Long amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TransactionType type; // CHARGE/USE/REFUND

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PayStatus status; // PENDING/COMPLETED/FAILED/REFUND_REQUESTED/REFUNDED

  @Column(name = "imp_uid")
  private String impUid;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) createdAt = LocalDateTime.now();
  }

  public void setStatus(PayStatus status) {
    this.status = status;
  }
}
