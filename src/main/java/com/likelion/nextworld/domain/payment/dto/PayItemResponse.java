package com.likelion.nextworld.domain.payment.dto;

import java.time.LocalDateTime;

import com.likelion.nextworld.domain.payment.entity.PayStatus;
import com.likelion.nextworld.domain.payment.entity.TransactionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayItemResponse {
  private Long payId;
  private Long amount;
  private TransactionType type;
  private PayStatus status;
  private String impUid;
  private LocalDateTime createdAt;
}
