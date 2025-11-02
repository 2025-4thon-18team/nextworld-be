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
}
