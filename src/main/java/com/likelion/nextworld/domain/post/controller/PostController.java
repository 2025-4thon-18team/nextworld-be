package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "Post", description = "포스트 관리 API")
@SecurityRequirement(name = "Authorization")
public class PostController {

  private final PostService postService;

  @Operation(summary = "독립 포스트 생성", description = "작품에 종속되지 않은 독립 포스트를 생성합니다.")
  @PostMapping
  public ResponseEntity<PostResponseDto> createPost(
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token,
      @RequestBody PostRequestDto request) {
    return ResponseEntity.ok(postService.createPost(request, token));
  }

  @Operation(summary = "작품 회차 생성", description = "특정 작품의 회차로 포스트를 생성합니다.")
  @PostMapping("/works/{workId}")
  public ResponseEntity<PostResponseDto> createWorkPost(
      @Parameter(description = "작품 ID", required = true) @PathVariable Long workId,
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token,
      @RequestBody PostRequestDto request) {
    request.setWorkId(workId);
    return ResponseEntity.ok(postService.createPost(request, token));
  }

  @Operation(summary = "포스트 목록 조회", description = "포스트 목록을 조회합니다. workId로 필터링 가능합니다.")
  @GetMapping
  public ResponseEntity<List<PostResponseDto>> getAllPosts(
      @Parameter(description = "작품 ID") @RequestParam(required = false) Long workId) {
    if (workId != null) {
      return ResponseEntity.ok(postService.getWorkPosts(workId));
    } else {
      return ResponseEntity.ok(postService.getIndependentPosts());
    }
  }

  @Operation(summary = "포스트 상세 조회", description = "포스트 ID로 상세 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<PostResponseDto> getPostById(
      @Parameter(description = "포스트 ID", required = true) @PathVariable Long id) {
    return ResponseEntity.ok(postService.getPostById(id));
  }

  @Operation(summary = "임시저장", description = "포스트를 임시저장합니다.")
  @PostMapping("/drafts")
  public ResponseEntity<PostResponseDto> saveDraft(
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token,
      @RequestBody PostRequestDto request) {
    return ResponseEntity.ok(postService.saveDraft(request, token));
  }

  @Operation(summary = "임시저장 목록 조회", description = "본인의 임시저장 목록을 조회합니다.")
  @GetMapping("/drafts")
  public ResponseEntity<List<PostResponseDto>> getAllDrafts(
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token) {
    return ResponseEntity.ok(postService.getAllDrafts(token));
  }

  @Operation(summary = "임시저장 단일 조회", description = "임시저장 포스트를 조회합니다.")
  @GetMapping("/drafts/{id}")
  public ResponseEntity<PostResponseDto> getDraftById(
      @Parameter(description = "포스트 ID", required = true) @PathVariable Long id,
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token) {
    return ResponseEntity.ok(postService.getDraftById(id, token));
  }

  @Operation(summary = "포스트 수정", description = "포스트를 수정합니다. 작성자만 수정할 수 있습니다.")
  @PutMapping("/{id}")
  public ResponseEntity<PostResponseDto> updatePost(
      @Parameter(description = "포스트 ID", required = true) @PathVariable Long id,
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token,
      @RequestBody PostRequestDto request) {
    return ResponseEntity.ok(postService.updatePost(id, request, token));
  }

  @Operation(summary = "포스트 삭제", description = "포스트를 삭제합니다. 작성자만 삭제할 수 있습니다.")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePost(
      @Parameter(description = "포스트 ID", required = true) @PathVariable Long id,
      @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
          String token) {
    postService.deletePost(id, token);
    return ResponseEntity.ok("포스트가 성공적으로 삭제되었습니다. ID: " + id);
  }
}
