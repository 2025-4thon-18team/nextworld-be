package com.likelion.nextworld.domain.post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drafts")
@Getter
@Setter
@NoArgsConstructor
public class Draft {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id")
  private Work work; // NULL 가능

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post; // NULL 가능

  @Enumerated(EnumType.STRING)
  @Column(name = "draft_type", nullable = false)
  private DraftType draftType; // WORK, POST

  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

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
