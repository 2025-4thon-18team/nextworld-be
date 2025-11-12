package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.user.dto.*;
import com.likelion.nextworld.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관리 API")
public class AuthController {

  private final UserService userService;

  @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
  @PostMapping("/signup")
  public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
    SignupResponse response = userService.signup(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResponse response = userService.login(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다.")
  @SecurityRequirement(name = "Authorization")
  @PostMapping("/logout")
  public ResponseEntity<String> logout(
      @Parameter(description = "Bearer 토큰", required = true) @RequestHeader("Authorization")
          String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    userService.logout(token);
    return ResponseEntity.ok("로그아웃 되었습니다.");
  }

  @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
  @PostMapping("/refresh")
  public ResponseEntity<String> refresh(
      @Parameter(description = "Refresh Token", required = true) @RequestHeader("Refresh-Token")
          String refreshToken) {
    String newAccessToken = userService.refresh(refreshToken);
    return ResponseEntity.ok(newAccessToken);
  }

  @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
  @SecurityRequirement(name = "Authorization")
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(
      @Parameter(description = "Bearer 토큰", required = true) @RequestHeader("Authorization")
          String authHeader) {

    String token = authHeader.replace("Bearer ", "");
    UserProfileResponse response = userService.getMyProfile(token);
    return ResponseEntity.ok(response);
  }
}
