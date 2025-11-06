package com.likelion.nextworld.domain.post.dto;

import java.time.LocalDateTime;

import com.likelion.nextworld.domain.post.entity.CreationType;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.entity.WorkType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
  private Long id;
  private String title;
  private String content;
  private String authorName;
  private String workTitle; // 부모 작품 제목
  private WorkStatus status; // 작성 상태 (DRAFT, PUBLISHED)
  private WorkType workType; // SHORT, SERIALIZED
  private CreationType creationType; // ORIGINAL, DERIVATIVE
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // ✅ 엔티티 기반 생성자
  public PostResponseDto(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.authorName = post.getAuthor() != null ? post.getAuthor().getNickname() : null;
    this.workTitle = post.getParentWork() != null ? post.getParentWork().getTitle() : null;
    this.status = post.getStatus();
    this.workType = post.getWorkType();
    this.creationType = post.getCreationType();
    this.createdAt = post.getCreatedAt();
    this.updatedAt = post.getUpdatedAt();
  }
}
