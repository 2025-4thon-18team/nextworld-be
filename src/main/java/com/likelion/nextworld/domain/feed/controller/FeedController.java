package com.likelion.nextworld.domain.feed.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.feed.dto.ListResponse;
import com.likelion.nextworld.domain.feed.service.FeedService;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
@Tag(name = "Feed", description = "피드/타임라인 관련 API")
public class FeedController {

  private final FeedService feedService;

  @Operation(
      summary = "최근 피드 조회",
      description =
          """
          전체 작품(Work)과 포스트(Post)를 최신순으로 조회합니다.
          - Work: id 기준 최신순
          - Post: createdAt 기준 최신순
          """)
  @GetMapping("/recent")
  public ResponseEntity<BaseResponse<ListResponse>> getRecentFeed() {

    ListResponse result = feedService.getRecentFeed();
    return ResponseEntity.ok(BaseResponse.success("최근 피드를 조회했습니다.", result));
  }
}
