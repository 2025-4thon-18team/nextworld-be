package com.likelion.nextworld.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "환불 요청")
public class RefundRequest {
  @Schema(description = "아임포트 결제 고유번호", example = "imp_1234567890", required = true)
  private String impUid;
  
  @Schema(description = "환불할 금액", example = "1000", required = true)
  private int amount;
  
  @Schema(description = "환불 사유", example = "구매 취소", required = true)
  private String reason;
}
