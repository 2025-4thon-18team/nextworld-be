package com.likelion.nextworld.domain.post.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "post_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostStatistics {

  @Id
  @Column(name = "post_id")
  private Long postId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  @MapsId
  private Post post;

  @Column(name = "views_count", nullable = false)
  @Builder.Default
  private Long viewsCount = 0L;

  @Column(name = "comments_count", nullable = false)
  @Builder.Default
  private Long commentsCount = 0L;

  @Column(name = "rating", precision = 3, scale = 2)
  private BigDecimal rating;

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
