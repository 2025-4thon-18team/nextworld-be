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
  private Long id;

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
  @JoinColumn(name = "derivative_author_id")
  private User derivativeAuthor; // 창작자 ID (2차 창작 작가, NULL 가능)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_user_id")
  private User platformUser; // 플랫폼 관리자 ID (NULL 가능)

  // 분배 정보
  @Column(name = "original_author_amount")
  private Long originalAuthorAmount; // 원작 작가 분배 금액

  @Column(name = "derivative_author_amount")
  private Long derivativeAuthorAmount; // 2차 창작 작가 분배 금액

  @Column(name = "platform_amount")
  private Long platformAmount; // 플랫폼 분배 금액

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
