package com.likelion.nextworld.domain.payment.dto;

import lombok.Getter;

@Getter
public class UseRequest {
  private Long amount;
  private Long postId; // 결제할 Post ID
  private Long derivativeWorkId;
  private Long authorId;
}
