package com.likelion.nextworld.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
