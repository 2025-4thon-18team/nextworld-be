package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;
import java.util.List;

import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkResponseDto {

  private Long id;
  private WorkTypeEnum workType;

  private String title;
  private String description;
  private String coverImageUrl;

  private String category;

  private String serializationSchedule;

  private Boolean allowDerivative;

  // 태그 (WorkTag에서 가져옴)
  private List<String> tags;

  // 통계 (WorkStatistics에서 가져옴)
  private Long totalLikesCount;
  private Long totalViewsCount;
  private BigDecimal totalRating;

  private String authorName;

  private Long parentWorkId;
  private String parentWorkTitle;
}
