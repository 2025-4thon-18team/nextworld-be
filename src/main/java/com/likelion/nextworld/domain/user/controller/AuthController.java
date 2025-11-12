package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.user.dto.LoginRequest;
import com.likelion.nextworld.domain.user.dto.LoginResponse;
import com.likelion.nextworld.domain.user.dto.SignupRequest;
import com.likelion.nextworld.domain.user.dto.SignupResponse;
import com.likelion.nextworld.domain.user.dto.UserProfileResponse;
import com.likelion.nextworld.domain.user.service.UserService;
import com.likelion.nextworld.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
    SignupResponse response = userService.signup(request);
    return ResponseEntity.ok(BaseResponse.success("회원가입이 완료되었습니다.", response));
  }

  @PostMapping("/login")
  public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
    LoginResponse response = userService.login(request);
    return ResponseEntity.ok(BaseResponse.success("로그인 성공", response));
  }

  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(
      @RequestHeader("Authorization") String authHeader) {

    String token = authHeader.replace("Bearer ", "");
    userService.logout(token);

    return ResponseEntity.ok(BaseResponse.success("로그아웃 되었습니다.", null));
  }

  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<String>> refresh(
      @RequestHeader("Refresh-Token") String refreshToken) {

    String newAccessToken = userService.refresh(refreshToken);

    return ResponseEntity.ok(BaseResponse.success("액세스 토큰이 재발급되었습니다.", newAccessToken));
  }

  @GetMapping("/me")
  public ResponseEntity<BaseResponse<UserProfileResponse>> getMyProfile(
      @RequestHeader("Authorization") String authHeader) {

    String token = authHeader.replace("Bearer ", "");
    UserProfileResponse response = userService.getMyProfile(token);

    return ResponseEntity.ok(BaseResponse.success("프로필 조회 성공", response));
  }
}
