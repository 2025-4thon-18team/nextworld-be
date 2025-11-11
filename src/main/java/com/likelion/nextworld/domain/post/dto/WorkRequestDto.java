package com.likelion.nextworld.domain.post.dto;

import com.likelion.nextworld.domain.post.entity.WorkType;
import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkRequestDto {
  private WorkTypeEnum workType; // 필수: ORIGINAL, DERIVATIVE

  private String title;
  private String description;
  private String coverImageUrl;
  private String tags; // 구분자 문자열: "태그1|태그2|태그3"

  private String category;

  private String serializationSchedule; // 구분자 문자열: "월|화|수"

  private Boolean allowDerivative;
  private String guidelineRelation;
  private String guidelineContent;
  private String guidelineBackground;
  private String bannedWords; // 구분자 문자열: "금지어1|금지어2|금지어3"
}
