package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.user.dto.*;
import com.likelion.nextworld.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
    SignupResponse response = userService.signup(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResponse response = userService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    userService.logout(token);
    return ResponseEntity.ok("로그아웃 되었습니다.");
  }

  @PostMapping("/refresh")
  public ResponseEntity<String> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
    String newAccessToken = userService.refresh(refreshToken);
    return ResponseEntity.ok(newAccessToken);
  }

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(
      @RequestHeader("Authorization") String authHeader) {

    String token = authHeader.replace("Bearer ", "");
    UserProfileResponse response = userService.getMyProfile(token);
    return ResponseEntity.ok(response);
  }
}
