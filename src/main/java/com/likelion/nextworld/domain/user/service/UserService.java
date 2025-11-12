package com.likelion.nextworld.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.user.dto.*;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.JwtTokenProvider;
import com.likelion.nextworld.domain.user.security.TokenBlacklist;
import com.likelion.nextworld.global.service.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final TokenBlacklist tokenBlacklist;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final S3Uploader s3Uploader;

  public SignupResponse signup(SignupRequest request) {
    // 비밀번호 일치 확인
    if (!request.getPassword().equals(request.getPasswordConfirm())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    // 이메일 중복 체크
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // 유저 생성 및 저장
    User user =
        User.builder()
            .name(request.getName())
            .nickname(request.getNickname())
            .email(request.getEmail())
            .password(encodedPassword)
            .pointsBalance(0L)
            .createdAt(LocalDateTime.now())
            .build();

    userRepository.save(user);

    return new SignupResponse(user.getUserId(), user.getEmail(), user.getNickname());
  }

  // 로그인
  public LoginResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

    return new LoginResponse(accessToken, refreshToken, user.getEmail(), user.getNickname());
  }

  // 로그아웃 (블랙리스트 등록)
  public void logout(String token) {
    if (jwtTokenProvider.validateToken(token)) {
      tokenBlacklist.add(token);
    } else {
      throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    }
  }

  // 액세스 토큰 재발급
  public String refresh(String refreshToken) {
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    return jwtTokenProvider.generateAccessToken(userId);
  }

  // 내 정보 조회
  public UserProfileResponse getMyProfile(String token) {
    if (!jwtTokenProvider.validateToken(token)) {
      throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(token);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    return new UserProfileResponse(user);
  }

  // 프로필 수정
  public UserProfileResponse updateMyProfile(String token, UserProfileUpdateRequest request) {
    if (!jwtTokenProvider.validateToken(token)) {
      throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(token);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    if (request.getNickname() != null && !request.getNickname().isBlank()) {
      user.setNickname(request.getNickname());
    }

    if (request.getName() != null && !request.getName().isBlank()) {
      user.setName(request.getName());
    }

    if (request.getProfileImageUrl() != null) {
      user.setProfileImageUrl(request.getProfileImageUrl());
    }

    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    return new UserProfileResponse(user);
  }
}
