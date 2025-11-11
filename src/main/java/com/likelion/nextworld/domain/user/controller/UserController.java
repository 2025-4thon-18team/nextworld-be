package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.user.dto.UserProfileResponse;
import com.likelion.nextworld.domain.user.dto.UserProfileUpdateRequest;
import com.likelion.nextworld.domain.user.service.UserService;
import com.likelion.nextworld.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PatchMapping("/me/profile")
  public ResponseEntity<BaseResponse<UserProfileResponse>> updateMyProfile(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody UserProfileUpdateRequest request) {

    String token = authHeader.replace("Bearer ", "");
    UserProfileResponse response = userService.updateMyProfile(token, request);

    return ResponseEntity.ok(BaseResponse.success("프로필이 성공적으로 수정되었습니다.", response));
  }
}
