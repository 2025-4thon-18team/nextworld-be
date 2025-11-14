package com.likelion.nextworld.domain.revenue.entity;

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
public class RevenueSettlementHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false)
  private Long settledAmount;

  @Column(nullable = false)
  private Long previousBalance;

  @Column(nullable = false)
  private Long newBalance;

  @Column(nullable = false)
  private LocalDateTime settledAt;

  @PrePersist
  void prePersist() {
    if (settledAt == null) {
      settledAt = LocalDateTime.now();
    }
  }
}
