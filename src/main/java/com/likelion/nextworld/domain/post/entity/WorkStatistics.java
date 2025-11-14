package com.likelion.nextworld.domain.post.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "work_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkStatistics {

  @Id
  @Column(name = "work_id")
  private Long workId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  @MapsId
  private Work work;

  @Column(name = "total_likes_count", nullable = false)
  @Builder.Default
  private Long totalLikesCount = 0L;

  @Column(name = "total_views_count", nullable = false)
  @Builder.Default
  private Long totalViewsCount = 0L;

  @Column(name = "total_rating", precision = 3, scale = 2)
  private BigDecimal totalRating;

  @Column(name = "last_calculated_at")
  private LocalDateTime lastCalculatedAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    this.lastCalculatedAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
