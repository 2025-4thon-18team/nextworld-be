package com.likelion.nextworld.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorProfileResponse {
  private Long authorId;
  private String nickname;
  private String bio;
  private String contactEmail;
  private String twitter;
  private String profileImageUrl;
}
