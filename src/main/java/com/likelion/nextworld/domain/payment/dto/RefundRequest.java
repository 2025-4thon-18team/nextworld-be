package com.likelion.nextworld.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundRequest {
  private String impUid;
  private int amount;
  private String reason;
}
