package com.likelion.nextworld.domain.scrap.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrapResponse {

  private Long id;
  private String targetType;
  private Long targetId;
  private String title;
  private LocalDateTime createdAt;
}
