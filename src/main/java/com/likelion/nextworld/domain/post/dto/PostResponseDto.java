package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.likelion.nextworld.domain.post.entity.CreationType;
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
  private Boolean hasImage; // 이미지 포함 여부

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

  // 태그 (PostTag에서 가져옴)
  private List<String> tags;

  // 통계 (PostStatistics에서 가져옴)
  private Long viewsCount;
  private Long commentsCount;
  private BigDecimal rating;

  private WorkStatus status;
  private String aiCheck;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
