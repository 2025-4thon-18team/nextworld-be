package com.likelion.nextworld.domain.user.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorPostResponse {
  private Long postId;
  private Long workId;
  private String title;
  private String thumbnailUrl;
  private List<String> tags;
}
