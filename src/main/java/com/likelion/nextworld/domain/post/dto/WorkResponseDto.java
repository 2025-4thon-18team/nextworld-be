package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkResponseDto {
  private Long id;
  private WorkTypeEnum workType;

  private String title;
  private String description;
  private String coverImageUrl;
  private String tags; // 구분자 문자열

  private String category;

  private String serializationSchedule;

  private Boolean allowDerivative;
  private String guidelineRelation;
  private String guidelineContent;
  private String guidelineBackground;
  private String bannedWords; // 구분자 문자열

  private Long totalLikesCount;
  private Long totalViewsCount;
  private BigDecimal totalRating;

  private String authorName;

  // Work 엔티티 → WorkResponse 변환 생성자
  public WorkResponseDto(Work work) {
    this.id = work.getId();
    this.workType = work.getWorkType();
    this.title = work.getTitle();
    this.description = work.getDescription();
    this.coverImageUrl = work.getCoverImageUrl();
    this.tags = work.getTags();
    this.category = work.getCategory();
    this.serializationSchedule = work.getSerializationSchedule();
    this.allowDerivative = work.getAllowDerivative();
    this.guidelineRelation = work.getGuidelineRelation();
    this.guidelineContent = work.getGuidelineContent();
    this.guidelineBackground = work.getGuidelineBackground();
    this.bannedWords = work.getBannedWords();
    this.totalLikesCount = work.getTotalLikesCount();
    this.totalViewsCount = work.getTotalViewsCount();
    this.totalRating = work.getTotalRating();
    this.authorName = work.getAuthor() != null ? work.getAuthor().getNickname() : null;
  }
}
