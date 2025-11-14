package com.likelion.nextworld.domain.post.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRatingRequest {

  @NotNull(message = "평점은 필수 값입니다.")
  @DecimalMin(value = "0.00", message = "평점은 0.00 이상이어야 합니다.")
  @DecimalMax(value = "5.00", message = "평점은 5.00 이하이어야 합니다.")
  private BigDecimal score;
}
