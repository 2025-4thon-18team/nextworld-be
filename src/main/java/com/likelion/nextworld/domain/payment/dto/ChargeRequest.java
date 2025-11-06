package com.likelion.nextworld.domain.payment.dto;

import lombok.Getter;

@Getter
public class ChargeRequest {
  private String impUid;
  private Long amount;
}
