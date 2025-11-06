package com.likelion.nextworld.domain.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivativeWork {

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private DerivativeWork parentWork; // 부모 작품 (1차 창작물)

  @OneToMany(mappedBy = "parentWork", cascade = CascadeType.ALL)
  private List<DerivativeWork> childWorks = new ArrayList<>(); // 파생된 2차 창작물들
}
