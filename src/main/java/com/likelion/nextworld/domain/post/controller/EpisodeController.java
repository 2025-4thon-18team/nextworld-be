package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/episodes")
@Tag(name = "Episode", description = "Episode 전용 API")
public class EpisodeController {

  private final PostService postService;

  @Operation(summary = "회차 전체 조회", description = "모든 발행된 Episode를 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<PostResponseDto>>> getAllEpisodes() {
    return ResponseEntity.ok(
        BaseResponse.success("전체 EPISODE 조회 완료", postService.getAllEpisodes()));
  }

  @Operation(summary = "작품 회차 목록 조회")
  @GetMapping("/work/{workId}")
  public ResponseEntity<BaseResponse<List<PostResponseDto>>> getEpisodesByWork(
      @PathVariable Long workId) {
    return ResponseEntity.ok(
        BaseResponse.success("작품 EPISODE 목록 조회 완료", postService.getEpisodesByWork(workId)));
  }

  @Operation(summary = "이전 회차 조회")
  @GetMapping("/{id}/previous")
  public ResponseEntity<BaseResponse<PostResponseDto>> getPreviousEpisode(@PathVariable Long id) {
    return ResponseEntity.ok(
        BaseResponse.success("이전 회차 조회 완료", postService.getPreviousEpisode(id)));
  }

  @Operation(summary = "다음 회차 조회")
  @GetMapping("/{id}/next")
  public ResponseEntity<BaseResponse<PostResponseDto>> getNextEpisode(@PathVariable Long id) {
    return ResponseEntity.ok(BaseResponse.success("다음 회차 조회 완료", postService.getNextEpisode(id)));
  }
}
