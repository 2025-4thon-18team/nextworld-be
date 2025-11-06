package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/works/posts")
public class PostController {

  private final PostService postService;

  // 창작물 게시
  @PostMapping
  public PostResponseDto createWork(
      @RequestHeader("Authorization") String token, @RequestBody PostRequestDto request) {
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
