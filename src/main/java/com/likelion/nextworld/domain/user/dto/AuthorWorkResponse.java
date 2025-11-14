package com.likelion.nextworld.domain.user.dto;

import java.math.BigDecimal;
import java.util.List;

import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorWorkResponse {

  private Long id;
  private WorkTypeEnum workType;
  private String title;
  private String description;
  private String coverImageUrl;
  private String category;
  private String serializationSchedule;
  private Boolean allowDerivative;
  private List<String> tags;
  private Long totalLikesCount;
  private Long totalViewsCount;
  private BigDecimal totalRating;
  private String authorName;
  private Long parentWorkId;
  private String parentWorkTitle;
}
