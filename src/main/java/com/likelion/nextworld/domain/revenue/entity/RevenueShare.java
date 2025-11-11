package com.likelion.nextworld.domain.revenue.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.user.entity.User;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "revenue_shares")
public class RevenueShare {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long revenueId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_id", nullable = false)
  private Pay pay;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post; // 포스트 ID (판매된 포스트)

  // 수익 분배 대상
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "original_author_id")
  private User originalAuthor; // 원작자 ID (2차 창작인 경우, NULL 가능)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "derivative_author_id", nullable = false)
  private User derivativeAuthor; // 창작자 ID (포스트 작성자)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id", nullable = false)
  private User admin; // 관리자 ID

  // 분배 정보
  @Column(name = "share_each", nullable = false)
  private Long shareEach; // 각자 분배 포인트

  @Column(name = "value_each", nullable = false)
  private Long valueEach; // 각자 정산 금액

  @Column(name = "distributed_at")
  private LocalDateTime distributedAt;

  @PrePersist
  void prePersist() {
    if (distributedAt == null) {
      distributedAt = LocalDateTime.now();
    }
  }
}
