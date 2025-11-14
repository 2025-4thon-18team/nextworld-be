package com.likelion.nextworld.domain.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.likelion.nextworld.domain.post.entity.CreationType;
import com.likelion.nextworld.domain.post.entity.PostType;
import com.likelion.nextworld.domain.post.entity.WorkStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorPostResponse {

  private Long id;
  private String title;
  private Boolean hasImage;
  private Long workId;
  private String workTitle;
  private PostType postType;
  private Integer episodeNumber;
  private Long parentWorkId;
  private String parentWorkTitle;
  private String authorName;
  private CreationType creationType;
  private Boolean isPaid;
  private Long price;
  private List<String> tags;
  private Long viewsCount;
  private Long commentsCount;
  private BigDecimal rating;
  private WorkStatus status;
  private String aiCheck;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
