package com.likelion.nextworld.domain.revenue.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.user.entity.User;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RevenueShare {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long revenueId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_id", nullable = false)
  private Pay pay;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false)
  private Long shareAmount;

  private LocalDateTime distributedAt;

  @PrePersist
  void prePersist() {
    if (distributedAt == null) {
      distributedAt = LocalDateTime.now();
    }
  }
}
