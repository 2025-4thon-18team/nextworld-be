package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkType;
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
  private Long parentWorkId;
  private String parentWorkTitle; // 원작 제목

  private String title;
  private String description;
  private String coverImageUrl;
  private String tags; // 구분자 문자열

  private String universeName;
  private String universeDescription;
  private String category;

  private WorkType serializationType;
  private String serializationSchedule;
  private Boolean isSerializing;

  private Boolean allowDerivative;
  private String guidelineRelation;
  private String guidelineContent;
  private String guidelineBackground;
  private String bannedWords; // 구분자 문자열

  private Boolean allowDerivativeProfit;

  private Long totalLikesCount;
  private Long totalViewsCount;
  private BigDecimal totalRating;

  private String authorName;

  // Work 엔티티 → WorkResponse 변환 생성자
  public WorkResponseDto(Work work) {
    this.id = work.getId();
    this.workType = work.getWorkType();
    this.parentWorkId = work.getParentWork() != null ? work.getParentWork().getId() : null;
    this.parentWorkTitle = work.getParentWork() != null ? work.getParentWork().getTitle() : null;
    this.title = work.getTitle();
    this.description = work.getDescription();
    this.coverImageUrl = work.getCoverImageUrl();
    this.tags = work.getTags();
    this.universeName = work.getUniverseName();
    this.universeDescription = work.getUniverseDescription();
    this.category = work.getCategory();
    this.serializationType = work.getSerializationType();
    this.serializationSchedule = work.getSerializationSchedule();
    this.isSerializing = work.getIsSerializing();
    this.allowDerivative = work.getAllowDerivative();
    this.guidelineRelation = work.getGuidelineRelation();
    this.guidelineContent = work.getGuidelineContent();
    this.guidelineBackground = work.getGuidelineBackground();
    this.bannedWords = work.getBannedWords();
    this.allowDerivativeProfit = work.getAllowDerivativeProfit();
    this.totalLikesCount = work.getTotalLikesCount();
    this.totalViewsCount = work.getTotalViewsCount();
    this.totalRating = work.getTotalRating();
    this.authorName = work.getAuthor() != null ? work.getAuthor().getNickname() : null;
  }
}
