package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;

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
public class PostRatingResponse {

  private Long postId;

  /** 내가 준 점수(null 가능) */
  private BigDecimal myScore;

  /** 평균 점수 */
  private BigDecimal averageScore;

  /** 평가한 사람 수 */
  private Long ratingCount;
}
