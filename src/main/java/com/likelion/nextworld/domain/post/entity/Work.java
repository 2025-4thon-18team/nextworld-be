package com.likelion.nextworld.domain.post.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "works")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"episodes"}) // ✅ 무한루프 방지
public class Work {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 작품 타입 (1차/2차)
  @Enumerated(EnumType.STRING)
  @Column(name = "work_type", nullable = false)
  private WorkTypeEnum workType; // ORIGINAL, DERIVATIVE

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  // 1. 기본 정보
  private String title;
  private String description;
  private String coverImageUrl;

  // 태그 (구분자로 구분: 태그1|태그2|태그3)
  @Column(columnDefinition = "TEXT")
  private String tags;

  // 카테고리
  private String category;

  @Column(name = "serialization_schedule", columnDefinition = "TEXT")
  private String serializationSchedule; // 구분자로 구분된 연재 일정

  // 4. 2차 창작 관련
  @Column(name = "allow_derivative", nullable = false)
  private Boolean allowDerivative = false;

  @Column(name = "guideline_relation", columnDefinition = "TEXT")
  private String guidelineRelation;

  @Column(name = "guideline_content", columnDefinition = "TEXT")
  private String guidelineContent;

  @Column(name = "guideline_background", columnDefinition = "TEXT")
  private String guidelineBackground;

  // 금지어 목록 (구분자로 구분: 금지어1|금지어2|금지어3)
  @Column(name = "banned_words", columnDefinition = "TEXT")
  private String bannedWords;

  // 5. 통계 (작품의 모든 포스트 집계)
  @Column(name = "total_likes_count", nullable = false)
  private Long totalLikesCount = 0L;

  @Column(name = "total_views_count", nullable = false)
  private Long totalViewsCount = 0L;

  @Column(name = "total_rating", precision = 3, scale = 2)
  private BigDecimal totalRating;

  // 타임스탬프
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  // 작품의 회차 (work_id로 연결된 Post들)
  @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> episodes = new ArrayList<>();

  // 양방향 편의 메서드
  public void addEpisode(Post post) {
    episodes.add(post);
    post.setWork(this);
  }
}
