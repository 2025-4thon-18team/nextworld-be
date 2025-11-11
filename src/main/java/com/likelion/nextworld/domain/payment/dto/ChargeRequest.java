package com.likelion.nextworld.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "포인트 충전 요청")
public class ChargeRequest {
  @Schema(description = "아임포트 결제 고유번호", example = "imp_1234567890", required = true)
  private String impUid;

  @Schema(description = "충전할 포인트 금액", example = "10000", required = true)
  private Long amount;
}
