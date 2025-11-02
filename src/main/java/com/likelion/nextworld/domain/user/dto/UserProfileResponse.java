package com.likelion.nextworld.domain.user.dto;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.Getter;

@Getter
public class UserProfileResponse {
  private Long userId;
  private String email;
  private String nickname;
  private Long pointsBalance;
  private Long totalEarned;
  private String guideline;

  public UserProfileResponse(User user) {
    this.userId = user.getUserId();
    this.email = user.getEmail();
    this.nickname = user.getNickname();
    this.pointsBalance = user.getPointsBalance();
    this.totalEarned = user.getTotalEarned();
    this.guideline = user.getGuideline();
  }
}
