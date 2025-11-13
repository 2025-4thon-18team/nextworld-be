package com.likelion.nextworld.domain.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

  @Column(name = "post_id")
  private Long postId;

  @Column(name = "work_id")
  private Long workId;

  @PrePersist
  void prePersist() {
    if (createdAt == null) createdAt = LocalDateTime.now();
  }

  public void setStatus(PayStatus status) {
    this.status = status;
  }

  public static Pay createCharge(User payer, Long amount, String impUid) {
    Pay pay = new Pay();
    pay.payer = payer;
    pay.amount = amount;
    pay.type = TransactionType.CHARGE;
    pay.status = PayStatus.PENDING;
    pay.impUid = impUid;
    pay.createdAt = LocalDateTime.now();
    return pay;
  }

  public static Pay createUse(User payer, Long amount, Long postId, Long workId) {
    Pay pay = new Pay();
    pay.payer = payer;
    pay.amount = amount;
    pay.type = TransactionType.USE;
    pay.status = PayStatus.COMPLETED;
    pay.postId = postId;
    pay.workId = workId;
    pay.createdAt = LocalDateTime.now();
    return pay;
  }
}
