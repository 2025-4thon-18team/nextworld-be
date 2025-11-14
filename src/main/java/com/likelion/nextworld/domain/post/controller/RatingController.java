package com.likelion.nextworld.domain.post.controller;

import com.likelion.nextworld.domain.post.dto.PostRatingRequest;
import com.likelion.nextworld.domain.post.dto.PostRatingResponse;
import com.likelion.nextworld.domain.post.service.RatingService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "Rating", description = "별점 관련 API")
public class RatingController {

  private final RatingService ratingService;

  @Operation(summary = "포스트 별점 등록/수정", description = "유저가 포스트에 별점을 등록 또는 수정합니다.")
  @PostMapping("/{postId}/rating")
  public ResponseEntity<BaseResponse<PostRatingResponse>> ratePost(
      @PathVariable Long postId,
      @Valid @RequestBody PostRatingRequest request,
      @AuthenticationPrincipal UserPrincipal user) {

    PostRatingResponse response = ratingService.ratePost(postId, request, user);
    return ResponseEntity.ok(BaseResponse.success("별점 등록/수정 완료", response));
  }

  @Operation(summary = "내가 준 별점 조회", description = "해당 포스트에 대해 내가 매긴 별점을 조회합니다.")
  @GetMapping("/{postId}/rating/me")
  public ResponseEntity<BaseResponse<PostRatingResponse>> getMyRating(
      @PathVariable Long postId, @AuthenticationPrincipal UserPrincipal user) {

    PostRatingResponse response = ratingService.getMyRating(postId, user);
    return ResponseEntity.ok(BaseResponse.success("내 별점 조회 완료", response));
  }

  @Operation(summary = "포스트 별점 요약", description = "평균 별점 및 평가 인원 수를 조회합니다.")
  @GetMapping("/{postId}/rating/summary")
  public ResponseEntity<BaseResponse<PostRatingResponse>> getRatingSummary(
      @PathVariable Long postId) {

    PostRatingResponse response = ratingService.getRatingSummary(postId);
    return ResponseEntity.ok(BaseResponse.success("별점 요약 조회 완료", response));
  }
}
