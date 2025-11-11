package com.likelion.nextworld.domain.post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id")
  private Work work; // NULL 가능

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post; // NULL 가능

  @Column(columnDefinition = "TEXT")
  private String reason;

  @Enumerated(EnumType.STRING)
  @Column(name = "report_status")
  private ReportStatus reportStatus = ReportStatus.PENDING;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "reviewed_at")
  private LocalDateTime reviewedAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    if (this.reportStatus == null) {
      this.reportStatus = ReportStatus.PENDING;
    }
  }
}
