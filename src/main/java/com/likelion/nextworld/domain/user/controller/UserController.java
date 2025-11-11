package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.user.dto.UserProfileResponse;
import com.likelion.nextworld.domain.user.dto.UserProfileUpdateRequest;
import com.likelion.nextworld.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 관리 API")
@SecurityRequirement(name = "Authorization")
public class UserController {

  private final UserService userService;

  @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필을 수정합니다.")
  @PatchMapping("/me/profile")
  public ResponseEntity<UserProfileResponse> updateMyProfile(
      @Parameter(description = "Bearer 토큰", required = true) @RequestHeader("Authorization")
          String authHeader,
      @RequestBody UserProfileUpdateRequest request) {

    String token = authHeader.replace("Bearer ", "");
    UserProfileResponse response = userService.updateMyProfile(token, request);
    return ResponseEntity.ok(response);
  }
}
