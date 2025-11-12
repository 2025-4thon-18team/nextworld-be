package com.likelion.nextworld.domain.like.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {

  private Long id;
  private Long workId;
  private String workName;
  private LocalDateTime createdAt;
}
