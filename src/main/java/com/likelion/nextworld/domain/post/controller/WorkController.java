package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkGuidelineResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkRequestDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;
import com.likelion.nextworld.domain.post.service.WorkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
@Tag(name = "Work", description = "작품 관리 API")
@SecurityRequirement(name = "Authorization")
public class WorkController {

  private final WorkService workService;

  @Operation(summary = "작품 생성", description = "새로운 작품을 생성합니다.")
  @PostMapping
  public ResponseEntity<WorkResponseDto> createWork(
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token,
      @RequestBody WorkRequestDto request) {
    return ResponseEntity.ok(workService.createWork(request, token));
  }

  @Operation(summary = "작품 목록 조회", description = "작품 목록을 조회합니다. workType으로 필터링 가능합니다.")
  @GetMapping
  public ResponseEntity<List<WorkResponseDto>> getAllWorks(
      @Parameter(description = "작품 타입 (ORIGINAL, DERIVATIVE)") @RequestParam(required = false)
          WorkTypeEnum workType) {
    return ResponseEntity.ok(workService.getAllWorks(workType));
  }

  @Operation(summary = "작품 상세 조회", description = "작품 ID로 상세 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<WorkResponseDto> getWorkById(
      @Parameter(description = "작품 ID", required = true) @PathVariable Long id) {
    return ResponseEntity.ok(workService.getWorkById(id));
  }

  @Operation(summary = "작품 회차 목록 조회", description = "특정 작품의 회차 목록을 조회합니다.")
  @GetMapping("/{workId}/posts")
  public ResponseEntity<List<PostResponseDto>> getWorkEpisodes(
      @Parameter(description = "작품 ID", required = true) @PathVariable Long workId) {
    return ResponseEntity.ok(workService.getWorkEpisodes(workId));
  }

  @Operation(summary = "2차 창작 포스트 목록 조회", description = "특정 작품을 원작으로 참조하는 포스트 목록을 조회합니다.")
  @GetMapping("/{workId}/derivatives")
  public ResponseEntity<List<PostResponseDto>> getDerivativePosts(
      @Parameter(description = "작품 ID", required = true) @PathVariable Long workId) {
    return ResponseEntity.ok(workService.getDerivativePosts(workId));
  }

  @Operation(summary = "작품 삭제", description = "작품을 삭제합니다. 작성자만 삭제할 수 있습니다.")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteWork(
      @Parameter(description = "작품 ID", required = true) @PathVariable Long id,
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token) {
    workService.deleteWork(id, token);
    return ResponseEntity.ok("작품이 성공적으로 삭제되었습니다. ID: " + id);
  }

  @Operation(summary = "작품 가이드라인 조회", description = "작품의 가이드라인 및 금지어를 조회합니다.")
  @GetMapping("/{workId}/guideline")
  public ResponseEntity<WorkGuidelineResponseDto> getWorkGuideline(
      @Parameter(description = "작품 ID", required = true) @PathVariable Long workId) {
    return ResponseEntity.ok(workService.getWorkGuideline(workId));
  }
}
