package com.likelion.nextworld.domain.post.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkRequest {
  private String title;
  private String description;
  private String coverImageUrl;
  private List<String> tags;

  private String universeDescription;

  private Boolean allowDerivative;
  private String guidelineRelation;
  private String guidelineContent;
  private String guidelineBackground;
  private List<String> bannedWords;

  private Boolean isPaid;
  private Boolean allowDerivativeProfit;
}
