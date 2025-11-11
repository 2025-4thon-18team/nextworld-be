package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;

  // 독립 포스트 생성
  @PostMapping
  public ResponseEntity<PostResponseDto> createPost(
      @RequestHeader("Authorization") String token, @RequestBody PostRequestDto request) {
    return ResponseEntity.ok(postService.createPost(request, token));
  }

  // 작품에 종속된 포스트 생성 (작품 회차)
  @PostMapping("/works/{workId}")
  public ResponseEntity<PostResponseDto> createWorkPost(
      @PathVariable Long workId,
      @RequestHeader("Authorization") String token,
      @RequestBody PostRequestDto request) {
    request.setWorkId(workId);
    return ResponseEntity.ok(postService.createPost(request, token));
  }

  // 포스트 목록 조회 (workId 필터링 가능, 없으면 독립 포스트만)
  @GetMapping
  public ResponseEntity<List<PostResponseDto>> getAllPosts(
      @RequestParam(required = false) Long workId) {
    if (workId != null) {
      return ResponseEntity.ok(postService.getWorkPosts(workId));
    } else {
      return ResponseEntity.ok(postService.getIndependentPosts());
    }
  }

  // 포스트 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
    return ResponseEntity.ok(postService.getPostById(id));
  }

  // 임시저장
  @PostMapping("/drafts")
  public ResponseEntity<PostResponseDto> saveDraft(
      @RequestHeader("Authorization") String token, @RequestBody PostRequestDto request) {
    return ResponseEntity.ok(postService.saveDraft(request, token));
  }

  // 임시저장 전체 조회 (본인만)
  @GetMapping("/drafts")
  public ResponseEntity<List<PostResponseDto>> getAllDrafts(
      @RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(postService.getAllDrafts(token));
  }

  // 임시저장 단일 조회 (본인만)
  @GetMapping("/drafts/{id}")
  public ResponseEntity<PostResponseDto> getDraftById(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(postService.getDraftById(id, token));
  }

  // 포스트 수정
  @PutMapping("/{id}")
  public ResponseEntity<PostResponseDto> updatePost(
      @PathVariable Long id,
      @RequestHeader("Authorization") String token,
      @RequestBody PostRequestDto request) {
    return ResponseEntity.ok(postService.updatePost(id, request, token));
  }

  // 포스트 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePost(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    postService.deletePost(id, token);
    return ResponseEntity.ok("포스트가 성공적으로 삭제되었습니다. ID: " + id);
  }
}
