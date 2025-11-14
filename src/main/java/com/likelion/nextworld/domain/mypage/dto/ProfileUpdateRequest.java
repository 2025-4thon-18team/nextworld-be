package com.likelion.nextworld.domain.mypage.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {
  private String name;
  private String bio;
  private String contactEmail;
  private String twitter;
  private MultipartFile profileImage;
}
