package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.user.dto.UserProfileResponse;
import com.likelion.nextworld.domain.user.dto.UserProfileUpdateRequest;
import com.likelion.nextworld.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PatchMapping("/me/profile")
  public ResponseEntity<UserProfileResponse> updateMyProfile(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody UserProfileUpdateRequest request) {

    String token = authHeader.replace("Bearer ", "");
    UserProfileResponse response = userService.updateMyProfile(token, request);
    return ResponseEntity.ok(response);
  }
}
