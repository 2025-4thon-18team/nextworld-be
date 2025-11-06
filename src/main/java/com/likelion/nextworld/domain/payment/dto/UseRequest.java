package com.likelion.nextworld.domain.payment.dto;

import lombok.Getter;

@Getter
public class UseRequest {
  private Long amount;
  private Long derivativeWorkId;
  private Long authorId;
}
