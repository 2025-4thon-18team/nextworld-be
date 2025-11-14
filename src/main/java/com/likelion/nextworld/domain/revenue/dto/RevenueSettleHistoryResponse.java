package com.likelion.nextworld.domain.revenue.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RevenueSettleHistoryResponse {
  private Long settledAmount;
  private Long previousBalance;
  private Long newBalance;
  private LocalDateTime settledAt;
}
