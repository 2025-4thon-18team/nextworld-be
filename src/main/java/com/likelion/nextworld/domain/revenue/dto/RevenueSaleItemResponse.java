package com.likelion.nextworld.domain.revenue.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RevenueSaleItemResponse {
  private String postTitle;
  private String buyerNickname;
  private Long amount;
  private LocalDateTime date;
}
