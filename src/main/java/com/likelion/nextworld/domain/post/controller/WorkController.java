package com.likelion.nextworld.domain.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.post.dto.WorkRequest;
import com.likelion.nextworld.domain.post.dto.WorkResponse;
import com.likelion.nextworld.domain.post.service.WorkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

  private final WorkService workService;

  @PostMapping
  public ResponseEntity<WorkResponse> createWork(
      @RequestHeader("Authorization") String token, @RequestBody WorkRequest request) {

    return ResponseEntity.ok(workService.createWork(request, token));
  }

  @DeleteMapping("/{id}")
  public String deleteWork(@PathVariable Long id, @RequestHeader("Authorization") String token) {
    workService.deleteWork(id, token); //  1차 창작물 삭제
    return "작품이 성공적으로 삭제되었습니다. ID: " + id;
  }
}
