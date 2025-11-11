package com.likelion.nextworld.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "포인트 사용 요청")
public class UseRequest {
  @Schema(description = "사용할 포인트 금액", example = "1000", required = true)
  private Long amount;

  @Schema(description = "결제할 Post ID", example = "1")
  private Long postId; // 결제할 Post ID

  @Schema(description = "파생 작품 ID", example = "1")
  private Long derivativeWorkId;

  @Schema(description = "작가 ID", example = "1")
  private Long authorId;
}
