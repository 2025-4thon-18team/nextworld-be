package com.likelion.nextworld.domain.revenue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "수익 분배 요청")
public class DistributeRequest {
  @Schema(description = "결제 ID", example = "1")
  private Long payId;
  
  @Schema(description = "수익 분배할 Post ID", example = "1")
  private Long postId; // 수익 분배할 Post ID
}

