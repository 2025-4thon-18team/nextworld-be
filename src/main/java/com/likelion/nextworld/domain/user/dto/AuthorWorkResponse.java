package com.likelion.nextworld.domain.user.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorWorkResponse {
  private Long workId;
  private String title;
  private String coverImageUrl;
  private String category;
  private String workType;
  private List<String> tags;
}
