package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;
import com.likelion.nextworld.global.ai.AiCheckService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/works/posts")
public class PostController {

  private final PostService postService;
  private final AiCheckService aiCheckService;

  // 2차 창작물 게시 (AI 검수 포함)
  @PostMapping
  public PostResponseDto createWork(
      @RequestHeader("Authorization") String token, @RequestBody PostRequestDto request) {

    // 1️⃣ AI 가이드라인 검수 수행
    boolean isSafe = aiCheckService.validatePostById(request.getWorkId(), request.getContent());
    if (!isSafe) {
      throw new IllegalArgumentException("가이드라인 또는 금지어 위반으로 업로드 불가합니다.");
    }

    // 2️⃣ 통과 시 기존 서비스 로직 실행
    return postService.createWork(request, token);
  }

  // 2차 창작물 임시저장
  @PostMapping("/save")
  public PostResponseDto saveDraft(
      @RequestBody PostRequestDto request, @RequestHeader("Authorization") String token) {
    return postService.saveDraft(request, token);
  }

  // 임시저장 전체 조회 (본인만)
  @GetMapping("/drafts/all")
  public List<PostResponseDto> getAllDrafts(@RequestHeader("Authorization") String token) {
    return postService.getAllDrafts(token);
  }

  // 임시저장 단일 조회 (본인만)
  @GetMapping("/drafts/{id}")
  public PostResponseDto getDraftById(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    return postService.getDraftById(id, token);
  }

  @PatchMapping("continue/{id}")
  public PostResponseDto updateWork(
      @PathVariable Long id,
      @RequestBody PostRequestDto request,
      @RequestHeader("Authorization") String token) {
    return postService.updateWork(id, request, token);
  }

  @DeleteMapping("drafts/{id}")
  public String deleteWork(@PathVariable Long id, @RequestHeader("Authorization") String token) {
    postService.deleteWork(id, token);
    return "삭제가 완료되었습니다. ID: " + id;
  }
}
