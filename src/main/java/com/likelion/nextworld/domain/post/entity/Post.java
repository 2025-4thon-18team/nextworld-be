package com.likelion.nextworld.domain.post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter private String title;

  @Setter
  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dauthor_id")
  private User dAuthor;

  @Setter
  @Enumerated(EnumType.STRING)
  private WorkStatus status; // DRAFT or PUBLISHED

  @Enumerated(EnumType.STRING)
  private WorkType workType; // SHORT, SERIALIZED

  @Enumerated(EnumType.STRING)
  private CreationType creationType; // ORIGINAL, DERIVATIVE

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    if (this.status == null) this.status = WorkStatus.DRAFT;
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  //  1차 창작물과의 연결 (핵심)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id")
  private Work parentWork;

  //  연관관계 편의 메서드 추가
  public void setParentWork(Work work) {
    this.parentWork = work;
  }
}
