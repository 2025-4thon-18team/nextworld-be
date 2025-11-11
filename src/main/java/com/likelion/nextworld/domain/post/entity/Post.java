package com.likelion.nextworld.domain.post.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 소속 작품 (작품 회차인 경우)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id")
  private Work work; // 작품 회차인 경우

  // 원작 참조 (원작 작품 지정)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_work_id")
  private Work parentWork; // 원작 참조

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  // 포스트 정보
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "has_image", nullable = false)
  @Builder.Default
  private Boolean hasImage = false; // 이미지 포함 여부

  // 포스트 타입
  @Enumerated(EnumType.STRING)
  @Column(name = "post_type", nullable = false)
  @Builder.Default
  private PostType postType = PostType.POST; // POST, EPISODE

  @Enumerated(EnumType.STRING)
  @Column(name = "creation_type")
  private CreationType creationType; // ORIGINAL, DERIVATIVE (NULL 가능)

  // 회차 번호 (작품 회차인 경우)
  @Column(name = "episode_number")
  private Integer episodeNumber;

  // 유료 설정
  @Column(name = "is_paid", nullable = false)
  @Builder.Default
  private Boolean isPaid = false;

  private Long price;

  // 태그 (구분자로 구분: 태그1|태그2|태그3)
  @Column(columnDefinition = "TEXT")
  private String tags;

  // 통계 (캐시)
  @Column(name = "likes_count", nullable = false)
  @Builder.Default
  private Long likesCount = 0L;

  @Column(name = "views_count", nullable = false)
  @Builder.Default
  private Long viewsCount = 0L;

  @Column(name = "comments_count", nullable = false)
  @Builder.Default
  private Long commentsCount = 0L;

  @Column(precision = 3, scale = 2)
  private BigDecimal rating;

  // 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private WorkStatus status = WorkStatus.DRAFT; // DRAFT, PUBLISHED, ARCHIVED

  // AI 검수
  @Column(name = "ai_check", columnDefinition = "TEXT")
  private String aiCheck;

  // 타임스탬프
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    if (this.status == null) {
      this.status = WorkStatus.DRAFT;
    }
    if (this.postType == null) {
      this.postType = PostType.POST;
    }
    if (this.isPaid == null) {
      this.isPaid = false;
    }
    if (this.likesCount == null) {
      this.likesCount = 0L;
    }
    if (this.viewsCount == null) {
      this.viewsCount = 0L;
    }
    if (this.commentsCount == null) {
      this.commentsCount = 0L;
    }
    if (this.hasImage == null) {
      this.hasImage = false;
    }
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  // 연관관계 편의 메서드
  public void setWork(Work work) {
    this.work = work;
  }

  public void setParentWork(Work work) {
    this.parentWork = work;
  }
}
