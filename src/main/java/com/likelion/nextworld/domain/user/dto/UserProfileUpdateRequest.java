package com.likelion.nextworld.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "프로필 수정 요청")
public class UserProfileUpdateRequest {
  @Schema(description = "닉네임", example = "사용자닉네임")
  private String nickname;

  @Schema(description = "이름", example = "홍길동")
  private String name;

  @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
  private String profileImageUrl;
}
