package com.likelion.nextworld.domain.revenue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RevenueSettleResponse {
  private Long totalSettledAmount;
  private Long remainingUnsettled;
  private Long newPointsBalance;
}
