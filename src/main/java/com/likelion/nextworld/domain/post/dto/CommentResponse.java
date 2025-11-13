package com.likelion.nextworld.domain.post.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {

  private Long id;
  private Long postId;
  private Long parentCommentId;
  private Long authorId;
  private String authorName;
  private String authorImageUrl;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
