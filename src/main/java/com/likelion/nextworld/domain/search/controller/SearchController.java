package com.likelion.nextworld.domain.search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.search.dto.SearchResponse;
import com.likelion.nextworld.domain.search.service.SearchService;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "Search", description = "작품 및 포스트 통합 검색 API")
public class SearchController {

  private final SearchService searchService;

  @Operation(
      summary = "작품/포스트 통합 검색",
      description =
          """
          주어진 검색어로 Work(원작)와 Post(2차 창작물)를 통합 검색합니다.
          - Work: 제목, 설명(description)에서 검색
          - Post: 제목, 본문(content)에서 검색
          """)
  @GetMapping
  public ResponseEntity<BaseResponse<SearchResponse>> search(
      @RequestParam("keyword") String keyword) {

    SearchResponse result = searchService.search(keyword);
    return ResponseEntity.ok(BaseResponse.success("검색이 완료되었습니다.", result));
  }
}
