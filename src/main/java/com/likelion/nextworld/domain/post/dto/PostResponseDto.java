package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.likelion.nextworld.domain.post.entity.CreationType;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostType;
import com.likelion.nextworld.domain.post.entity.WorkStatus;

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
  private String thumbnailUrl;

  private Long workId; // 소속 작품 ID
  private String workTitle; // 소속 작품 제목
  private PostType postType;
  private Integer episodeNumber;

  private Long parentWorkId; // 원작 작품 ID
  private String parentWorkTitle; // 원작 작품 제목

  private String authorName;
  private CreationType creationType;

  private Boolean isPaid;
  private Long price;

  private String tags; // 구분자 문자열

  private Long likesCount;
  private Long viewsCount;
  private Long commentsCount;
  private BigDecimal rating;

  private WorkStatus status;
  private String aiCheck;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // ✅ 엔티티 기반 생성자
  public PostResponseDto(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.thumbnailUrl = post.getThumbnailUrl();
    this.workId = post.getWork() != null ? post.getWork().getId() : null;
    this.workTitle = post.getWork() != null ? post.getWork().getTitle() : null;
    this.postType = post.getPostType();
    this.episodeNumber = post.getEpisodeNumber();
    this.parentWorkId = post.getParentWork() != null ? post.getParentWork().getId() : null;
    this.parentWorkTitle = post.getParentWork() != null ? post.getParentWork().getTitle() : null;
    this.authorName = post.getAuthor() != null ? post.getAuthor().getNickname() : null;
    this.creationType = post.getCreationType();
    this.isPaid = post.getIsPaid();
    this.price = post.getPrice();
    this.tags = post.getTags();
    this.likesCount = post.getLikesCount();
    this.viewsCount = post.getViewsCount();
    this.commentsCount = post.getCommentsCount();
    this.rating = post.getRating();
    this.status = post.getStatus();
    this.aiCheck = post.getAiCheck();
    this.createdAt = post.getCreatedAt();
    this.updatedAt = post.getUpdatedAt();
  }
}
