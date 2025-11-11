package com.likelion.nextworld.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "결제 검증 요청")
public class VerifyRequest {
  @Schema(description = "아임포트 결제 고유번호", example = "imp_1234567890", required = true)
  private String impUid;
}
