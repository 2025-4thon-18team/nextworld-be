package com.likelion.nextworld.domain.user.dto;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.Getter;

@Getter
public class UserProfileResponse {
  private Long userId;
  private String email;
  private String nickname;
  private String name;
  private String profileImageUrl;
  private Long pointsBalance;

  public UserProfileResponse(User user) {
    this.userId = user.getUserId();
    this.email = user.getEmail();
    this.nickname = user.getNickname();
    this.name = user.getName();
    this.profileImageUrl = user.getProfileImageUrl();
    this.pointsBalance = user.getPointsBalance();
  }
}
