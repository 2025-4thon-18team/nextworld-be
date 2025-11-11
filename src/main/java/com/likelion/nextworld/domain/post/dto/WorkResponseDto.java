package com.likelion.nextworld.domain.post.dto;

import java.util.List;

import com.likelion.nextworld.domain.post.entity.Work;

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
  private Long price; // 금액
  private Boolean allowDerivativeProfit;

  private String authorName;

  // Work 엔티티 → WorkResponse 변환 생성자
  public WorkResponseDto(Work work) {
    this.id = work.getId();
    this.title = work.getTitle();
    this.description = work.getDescription();
    this.coverImageUrl = work.getCoverImageUrl();
    this.tags = work.getTags();

    this.universeDescription = work.getUniverseDescription();
    this.allowDerivative = work.getAllowDerivative();
    this.guidelineRelation = work.getGuidelineRelation();
    this.guidelineContent = work.getGuidelineContent();
    this.guidelineBackground = work.getGuidelineBackground();
    this.bannedWords = work.getBannedWords();

    this.isPaid = work.getIsPaid();
    this.allowDerivativeProfit = work.getAllowDerivativeProfit();

    //  작성자 닉네임 (User 엔티티의 nickname)
    this.authorName = work.getAuthor() != null ? work.getAuthor().getNickname() : null;
  }
}
