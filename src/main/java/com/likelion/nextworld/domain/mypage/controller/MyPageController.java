package com.likelion.nextworld.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.mypage.dto.ProfileUpdateRequest;
import com.likelion.nextworld.domain.mypage.service.MyPageService;
import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.dto.PointsResponse;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.scrap.service.ScrapService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService myPageService;
  private final ScrapService scrapService;

  @Operation(summary = "내 포인트 조회", description = "사용자의 현재 포인트 정보를 반환합니다.")
  @GetMapping("/points")
  public ResponseEntity<BaseResponse<PointsResponse>> points(
      @AuthenticationPrincipal UserPrincipal user) {

    PointsResponse response = myPageService.myPoints(user);

    return ResponseEntity.ok(BaseResponse.success("포인트 조회가 완료되었습니다.", response));
  }

  @Operation(summary = "결제 내역 조회", description = "사용자의 결제 내역 리스트를 반환합니다.")
  @GetMapping("/paylist")
  public ResponseEntity<BaseResponse<List<PayItemResponse>>> paylist(
      @AuthenticationPrincipal UserPrincipal user) {

    List<PayItemResponse> list = myPageService.myPayList(user);

    return ResponseEntity.ok(BaseResponse.success("결제 내역 조회가 완료되었습니다.", list));
  }

  @PutMapping(value = "/profile", consumes = "multipart/form-data")
  public ResponseEntity<?> updateProfile(
      @AuthenticationPrincipal UserPrincipal user, @ModelAttribute ProfileUpdateRequest request) {

    myPageService.updateProfile(user, request);
    return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다.");
  }

  @Operation(
      summary = "내 북마크 WORK 리스트 조회",
      description = "현재 로그인한 사용자가 스크랩한 WORK 목록을 조회합니다. (로그인 필요)")
  @GetMapping("/scraps/works")
  public ResponseEntity<BaseResponse<List<WorkResponseDto>>> getMyWorkScraps(
      @AuthenticationPrincipal UserPrincipal user) {

    List<WorkResponseDto> list = scrapService.getMyWorkScraps(user);
    return ResponseEntity.ok(BaseResponse.success("WORK 스크랩 목록 조회 완료", list));
  }

  @Operation(
      summary = "내 북마크 POST 리스트 조회",
      description = "현재 로그인한 사용자가 스크랩한 POST 목록을 조회합니다. (로그인 필요)")
  @GetMapping("/scraps/posts")
  public ResponseEntity<BaseResponse<List<PostResponseDto>>> getMyPostScraps(
      @AuthenticationPrincipal UserPrincipal user) {

    List<PostResponseDto> list = scrapService.getMyPostScraps(user);
    return ResponseEntity.ok(BaseResponse.success("POST 스크랩 목록 조회 완료", list));
  }
}
