package com.likelion.nextworld.domain.payment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentHistoryResponse {
  private String title; // 충전이면 "포인트 충전", 사용이면 게시글 제목
  private String opponentName; // 충전은 null, 사용은 구매자 닉네임
  private Long amount; // 금액 (+ 충전, - 사용)
  private LocalDateTime date;
}
