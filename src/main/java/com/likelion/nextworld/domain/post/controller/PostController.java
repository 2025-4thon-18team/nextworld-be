package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;
import com.likelion.nextworld.global.ai.AiCheckService;
import com.likelion.nextworld.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/works/posts")
public class PostController {

  private final PostService postService;
  private final AiCheckService aiCheckService;

  // 2차 창작물 게시 (AI 검수 포함)
  @PostMapping
  public ResponseEntity<BaseResponse<PostResponseDto>> createWork(
      @RequestHeader("Authorization") String token, @RequestBody PostRequestDto request) {

    // 1️⃣ AI 가이드라인 검수 수행
    boolean isSafe = aiCheckService.validatePostById(request.getWorkId(), request.getContent());
    if (!isSafe) {
      throw new IllegalArgumentException("가이드라인 또는 금지어 위반으로 업로드 불가합니다.");
    }

    // 2️⃣ 통과 시 기존 서비스 로직 실행
    PostResponseDto response = postService.createWork(request, token);

    return ResponseEntity.ok(BaseResponse.success("작품 게시가 완료되었습니다.", response));
  }

  // 2차 창작물 임시저장
  @PostMapping("/save")
  public ResponseEntity<BaseResponse<PostResponseDto>> saveDraft(
      @RequestBody PostRequestDto request, @RequestHeader("Authorization") String token) {

    PostResponseDto response = postService.saveDraft(request, token);

    return ResponseEntity.ok(BaseResponse.success("임시 저장되었습니다.", response));
  }

  // 임시저장 전체 조회 (본인만)
  @GetMapping("/drafts/all")
  public ResponseEntity<BaseResponse<List<PostResponseDto>>> getAllDrafts(
      @RequestHeader("Authorization") String token) {

    List<PostResponseDto> drafts = postService.getAllDrafts(token);

    return ResponseEntity.ok(BaseResponse.success("임시저장 목록 조회 완료", drafts));
  }

  // 임시저장 단일 조회 (본인만)
  @GetMapping("/drafts/{id}")
  public ResponseEntity<BaseResponse<PostResponseDto>> getDraftById(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {

    PostResponseDto response = postService.getDraftById(id, token);

    return ResponseEntity.ok(BaseResponse.success("임시저장 조회 완료", response));
  }

  // 이어쓰기
  @PatchMapping("continue/{id}")
  public ResponseEntity<BaseResponse<PostResponseDto>> updateWork(
      @PathVariable Long id,
      @RequestBody PostRequestDto request,
      @RequestHeader("Authorization") String token) {

    PostResponseDto updated = postService.updateWork(id, request, token);

    return ResponseEntity.ok(BaseResponse.success("수정이 완료되었습니다.", updated));
  }

  // 임시저장 삭제
  @DeleteMapping("drafts/{id}")
  public ResponseEntity<BaseResponse<Void>> deleteWork(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {

    postService.deleteWork(id, token);

    return ResponseEntity.ok(BaseResponse.success("삭제가 완료되었습니다. ID: " + id, null));
  }
}
