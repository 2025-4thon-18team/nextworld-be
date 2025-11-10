package com.likelion.nextworld.domain.payment.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChargeOptionsResponse {

  private Long currentPoints;
  private List<ChargeOption> chargeOptions;
  private List<String> paymentMethods;

  @Getter
  @AllArgsConstructor
  public static class ChargeOption {
    private Long chargePoint;
    private Long price;
    private Long expectedBalance;
  }
}
