package com.likelion.nextworld.domain.post.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.nextworld.domain.post.dto.WorkRequestDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.service.WorkService;
import com.likelion.nextworld.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

  private final WorkService workService;

  // 이미지 업로드만 (S3에 연결)
  @PostMapping(
      value = "/upload-image",
      consumes = {"multipart/form-data"})
  public ResponseEntity<BaseResponse<String>> uploadImage(
      @RequestHeader("Authorization") String token, @RequestPart("file") MultipartFile file)
      throws IOException {

    String imageUrl = workService.uploadImage(token, file);

    return ResponseEntity.ok(BaseResponse.success("이미지 업로드가 완료되었습니다.", imageUrl)); // URL만 반환
  }

  @PostMapping
  public ResponseEntity<BaseResponse<WorkResponseDto>> createWork(
      @RequestHeader("Authorization") String token, @RequestBody WorkRequestDto request) {

    WorkResponseDto response = workService.createWork(request, token);

    return ResponseEntity.ok(BaseResponse.success("작품이 성공적으로 생성되었습니다.", response));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<Void>> deleteWork(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {

    workService.deleteWork(id, token); // 1차 창작물 삭제

    return ResponseEntity.ok(BaseResponse.success("작품이 성공적으로 삭제되었습니다. ID: " + id, null));
  }
}
