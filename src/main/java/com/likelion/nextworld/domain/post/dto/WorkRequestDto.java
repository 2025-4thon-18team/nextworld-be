package com.likelion.nextworld.domain.post.dto;

import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkRequestDto {
  private WorkTypeEnum workType; // 필수: ORIGINAL, DERIVATIVE

  private Long parentWorkId; // 원작 작품 ID (DERIVATIVE인 경우 필수)

  private String title;
  private String description;
  private String coverImageUrl;

  private String category;

  private String serializationSchedule; // 연재 일정

  private Boolean allowDerivative;

  // 가이드라인 및 금지어 (WorkGuideline으로 저장)
  private String guidelineRelation;
  private String guidelineContent;
  private String guidelineBackground;
  private String bannedWords; // 금지어

  // 태그 (WorkTag로 저장)
  private java.util.List<String> tags; // 태그 이름 리스트
}
