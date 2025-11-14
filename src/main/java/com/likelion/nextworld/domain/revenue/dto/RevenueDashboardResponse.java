package com.likelion.nextworld.domain.revenue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RevenueDashboardResponse {
  private Long totalSalesCount;
  private Long totalRevenue;
  private Long originalAuthorFee;
  private Long platformFee;
  private Long netIncome;
}
