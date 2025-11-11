package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkRequestDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;
import com.likelion.nextworld.domain.post.service.WorkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

  private final WorkService workService;

  // 작품 생성
  @PostMapping
  public ResponseEntity<WorkResponseDto> createWork(
      @RequestHeader("Authorization") String token, @RequestBody WorkRequestDto request) {
    return ResponseEntity.ok(workService.createWork(request, token));
  }

  // 작품 목록 조회 (workType 필터링 가능)
  @GetMapping
  public ResponseEntity<List<WorkResponseDto>> getAllWorks(
      @RequestParam(required = false) WorkTypeEnum workType) {
    return ResponseEntity.ok(workService.getAllWorks(workType));
  }

  // 작품 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long id) {
    return ResponseEntity.ok(workService.getWorkById(id));
  }

  // 작품의 회차 목록 조회
  @GetMapping("/{workId}/posts")
  public ResponseEntity<List<PostResponseDto>> getWorkEpisodes(@PathVariable Long workId) {
    return ResponseEntity.ok(workService.getWorkEpisodes(workId));
  }

  // 작품의 원작 참조 포스트 목록 조회
  @GetMapping("/{workId}/derivatives")
  public ResponseEntity<List<PostResponseDto>> getDerivativePosts(@PathVariable Long workId) {
    return ResponseEntity.ok(workService.getDerivativePosts(workId));
  }

  // 작품 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteWork(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    workService.deleteWork(id, token);
    return ResponseEntity.ok("작품이 성공적으로 삭제되었습니다. ID: " + id);
  }
}
