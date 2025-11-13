package com.likelion.nextworld.domain.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchasedWorkResponse {

  private Long postId;
  private Long workId;

  private String title;
  private Long amount;
  private LocalDateTime purchasedAt;

  private String coverImageUrl;
  private String workType;
  private Long parentWorkId;
}
