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
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "MyPage", description = "마이페이지 API")
@SecurityRequirement(name = "Authorization")
public class MyPageController {

  private final MyPageService myPageService;

  @Operation(summary = "포인트 조회", description = "사용자의 현재 포인트 잔액을 조회합니다.")
  @GetMapping("/points")
  public ResponseEntity<PointsResponse> points(@AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(myPageService.myPoints(user));
  }

  @Operation(summary = "결제 내역 조회", description = "사용자의 결제 내역을 조회합니다.")
  @GetMapping("/paylist")
  public ResponseEntity<List<PayItemResponse>> paylist(
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(myPageService.myPayList(user));
  }

  @Operation(summary = "구매한 포스트 조회", description = "사용자가 구매한 포스트 목록을 조회합니다.")
  @GetMapping("/purchased/posts")
  public ResponseEntity<List<PostResponseDto>> getPurchasedPosts(
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(myPageService.getPurchasedPosts(user));
  }

  @Operation(summary = "구매한 작품 조회", description = "사용자가 구매한 작품 목록을 조회합니다.")
  @GetMapping("/purchased/works")
  public ResponseEntity<List<WorkResponseDto>> getPurchasedWorks(
      @AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(myPageService.getPurchasedWorks(user));
  }
}
