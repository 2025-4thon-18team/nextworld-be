package com.likelion.nextworld.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId; // 사용자ID

  @Column(nullable = false)
  private String name; // 이름

  @Column(nullable = false, unique = true)
  private String email; // 이메일

  @Column(nullable = false)
  private String password; // 비밀번호

  @Column(nullable = false)
  private String nickname; // 닉네임

  @Column(name = "points_balance", nullable = false)
  private Long pointsBalance; // 현재 보유 포인트

  @Column(name = "total_earned", nullable = false)
  private Long totalEarned; // 누적 수익

  @Column(columnDefinition = "TEXT")
  private String guideline; // 가이드라인

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt; // 가입일

  @Column(name = "updated_at")
  private LocalDateTime updatedAt; // 수정일

  private String profileImageUrl; // 프로필 이미지

  @Column(length = 200)
  private String bio; // 자기소개

  private String twitter; // 트위터 계정

  private String contactEmail; // 연락용 이메일

  // 포인트 적립
  public void increasePoints(Long amount) {
    if (amount == null || amount <= 0) {
      throw new IllegalArgumentException("적립할 포인트는 0보다 커야 합니다.");
    }
    this.pointsBalance += amount;
  }

  // 포인트 차감
  public void decreasePoints(Long amount) {
    if (amount == null || amount <= 0) {
      throw new IllegalArgumentException("차감할 포인트는 0보다 커야 합니다.");
    }
    if (this.pointsBalance < amount) {
      throw new IllegalArgumentException("보유 포인트가 부족합니다.");
    }
    this.pointsBalance -= amount;
  }
}
