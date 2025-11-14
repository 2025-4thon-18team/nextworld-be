package com.likelion.nextworld.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.like.dto.LikeResponse;
import com.likelion.nextworld.domain.like.service.LikeService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@Tag(name = "Like", description = "좋아요 관련 API")
public class LikeController {

  private final LikeService likeService;

  @Operation(
      summary = "좋아요 등록",
      description = """
          특정 게시글(workId)에 대해 좋아요를 등록합니다. (로그인 필요)
          """)
  @PostMapping("/{workId}")
  public ResponseEntity<BaseResponse<LikeResponse>> like(
      @PathVariable Long workId, @AuthenticationPrincipal UserPrincipal user) {

    LikeResponse res = likeService.createLike(workId, user);
    return ResponseEntity.ok(BaseResponse.success("좋아요 등록 완료", res));
  }

  @Operation(
      summary = "좋아요 취소",
      description = """
          특정 게시글(workId)에 대해 등록된 좋아요를 취소합니다. (로그인 필요)
          """)
  @DeleteMapping("/{workId}")
  public ResponseEntity<BaseResponse<String>> unlike(
      @PathVariable Long workId, @AuthenticationPrincipal UserPrincipal user) {

    likeService.deleteLike(workId, user);
    return ResponseEntity.ok(BaseResponse.success("좋아요 취소 완료"));
  }
}
