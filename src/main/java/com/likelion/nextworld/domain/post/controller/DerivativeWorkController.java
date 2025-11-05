package com.likelion.nextworld.domain.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.DerivativeWorkRequestDto;
import com.likelion.nextworld.domain.post.dto.DerivativeWorkResponseDto;
import com.likelion.nextworld.domain.post.service.DerivativeWorkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/works/derivative")
public class DerivativeWorkController {

  private final DerivativeWorkService derivativeWorkService;

  // 창작물 게시
  @PostMapping
  public DerivativeWorkResponseDto createWork(
      @RequestHeader("Authorization") String token, @RequestBody DerivativeWorkRequestDto request) {
    return derivativeWorkService.createWork(request, token);
  }

  // 2차 창작물 임시저장
  @PostMapping("/save")
  public DerivativeWorkResponseDto saveDraft(
      @RequestBody DerivativeWorkRequestDto request, @RequestHeader("Authorization") String token) {
    return derivativeWorkService.saveDraft(request, token);
  }

  // 임시저장 전체 조회 (본인만)
  @GetMapping("/drafts/all")
  public List<DerivativeWorkResponseDto> getAllDrafts(
      @RequestHeader("Authorization") String token) {
    return derivativeWorkService.getAllDrafts(token);
  }

  // 임시저장 단일 조회 (본인만)
  @GetMapping("/drafts/{id}")
  public DerivativeWorkResponseDto getDraftById(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    return derivativeWorkService.getDraftById(id, token);
  }

  @PatchMapping("continue/{id}")
  public DerivativeWorkResponseDto updateWork(
      @PathVariable Long id,
      @RequestBody DerivativeWorkRequestDto request,
      @RequestHeader("Authorization") String token) {
    return derivativeWorkService.updateWork(id, request, token);
  }

  @DeleteMapping("drafts/{id}")
  public String deleteWork(@PathVariable Long id, @RequestHeader("Authorization") String token) {
    derivativeWorkService.deleteWork(id, token);
    return "삭제가 완료되었습니다. ID: " + id;
  }
}
