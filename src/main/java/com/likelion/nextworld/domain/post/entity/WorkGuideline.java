package com.likelion.nextworld.domain.post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "work_guidelines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkGuideline {

  @Id
  @Column(name = "work_id")
  private Long workId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  @MapsId
  private Work work;

  @Column(name = "guideline_relation", columnDefinition = "TEXT")
  private String guidelineRelation;

  @Column(name = "guideline_content", columnDefinition = "TEXT")
  private String guidelineContent;

  @Column(name = "guideline_background", columnDefinition = "TEXT")
  private String guidelineBackground;

  @Column(name = "word")
  private String word; // 금지어

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
}
