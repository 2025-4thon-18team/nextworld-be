package com.likelion.nextworld.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkGuidelineResponseDto {
  private Long workId;
  private String workTitle;
  private String guidelineRelation;
  private String guidelineContent;
  private String guidelineBackground;
  private String bannedWords; // 금지어
}
