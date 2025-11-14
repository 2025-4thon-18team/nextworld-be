package com.likelion.nextworld.domain.post.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkGuidelineResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkRequestDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;
import com.likelion.nextworld.domain.post.service.WorkService;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
@Tag(name = "Work", description = "작품 관리 API")
public class WorkController {

  private final WorkService workService;

  @Operation(summary = "작품 생성", description = "새로운 작품을 생성합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<WorkResponseDto>> createWork(
      @RequestHeader("Authorization") String token, @RequestBody WorkRequestDto request) {

    WorkResponseDto response = workService.createWork(request, token);
    return ResponseEntity.ok(BaseResponse.success("작품 생성 완료", response));
  }

  @Operation(summary = "작품 목록 조회", description = "작품 목록을 조회합니다. workType으로 필터링 가능합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<WorkResponseDto>>> getAllWorks(
      @RequestParam(required = false) WorkTypeEnum workType) {

    List<WorkResponseDto> list = workService.getAllWorks(workType);
    return ResponseEntity.ok(BaseResponse.success("작품 목록 조회 완료", list));
  }

  @Operation(summary = "작품 상세 조회", description = "작품 ID로 상세 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<WorkResponseDto>> getWorkById(@PathVariable Long id) {

    WorkResponseDto response = workService.getWorkById(id);
    return ResponseEntity.ok(BaseResponse.success("작품 상세 조회 완료", response));
  }

  @Operation(summary = "작품 회차 목록 조회", description = "특정 작품의 회차 목록을 조회합니다.")
  @GetMapping("/{workId}/posts")
  public ResponseEntity<BaseResponse<List<PostResponseDto>>> getWorkEpisodes(
      @PathVariable Long workId) {

    List<PostResponseDto> list = workService.getWorkEpisodes(workId);
    return ResponseEntity.ok(BaseResponse.success("작품 회차 목록 조회 완료", list));
  }

  @Operation(summary = "2차 창작 포스트 목록 조회", description = "특정 작품을 원작으로 참조하는 포스트 목록을 조회합니다.")
  @GetMapping("/{workId}/derivatives")
  public ResponseEntity<BaseResponse<List<PostResponseDto>>> getDerivativePosts(
      @PathVariable Long workId) {

    List<PostResponseDto> list = workService.getDerivativePosts(workId);
    return ResponseEntity.ok(BaseResponse.success("2차 창작 포스트 목록 조회 완료", list));
  }

  @Operation(summary = "작품 삭제", description = "작품을 삭제합니다. 작성자만 삭제할 수 있습니다.")
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<Void>> deleteWork(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {

    workService.deleteWork(id, token);
    return ResponseEntity.ok(BaseResponse.success("작품 삭제 완료", null));
  }

  @Operation(summary = "작품 가이드라인 조회", description = "작품의 가이드라인 및 금지어를 조회합니다.")
  @GetMapping("/{workId}/guideline")
  public ResponseEntity<BaseResponse<WorkGuidelineResponseDto>> getWorkGuideline(
      @PathVariable Long workId) {

    WorkGuidelineResponseDto response = workService.getWorkGuideline(workId);
    return ResponseEntity.ok(BaseResponse.success("작품 가이드라인 조회 완료", response));
  }

  // 이미지 업로드 (S3)
  @PostMapping(
      value = "/upload-image",
      consumes = {"multipart/form-data"})
  public ResponseEntity<BaseResponse<String>> uploadImage(
      @RequestHeader("Authorization") String token, @RequestPart("file") MultipartFile file)
      throws IOException {

    String imageUrl = workService.uploadImage(token, file);
    return ResponseEntity.ok(BaseResponse.success("이미지 업로드 완료", imageUrl));
  }
}
