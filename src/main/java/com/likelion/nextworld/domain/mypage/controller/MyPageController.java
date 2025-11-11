package com.likelion.nextworld.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.mypage.dto.ProfileUpdateRequest;
import com.likelion.nextworld.domain.mypage.service.MyPageService;
import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.dto.PointsResponse;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService myPageService;

  @GetMapping("/points")
  public ResponseEntity<PointsResponse> points(@AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(myPageService.myPoints(user));
  }

  @GetMapping("/paylist")
  public ResponseEntity<List<PayItemResponse>> paylist(
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(myPageService.myPayList(user));
  }

  @PutMapping(value = "/profile", consumes = "multipart/form-data")
  public ResponseEntity<?> updateProfile(
      @AuthenticationPrincipal UserPrincipal user, @ModelAttribute ProfileUpdateRequest request) {

    myPageService.updateProfile(user, request);
    return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다.");
  }
}
