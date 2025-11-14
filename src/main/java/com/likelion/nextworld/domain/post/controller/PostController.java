package com.likelion.nextworld.domain.post.controller;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.service.PostService;
import com.likelion.nextworld.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "Post", description = "포스트 관리 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "독립 포스트 생성", description = "작품에 종속되지 않은 독립 포스트를 생성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<PostResponseDto>> createPost(
            @Parameter(description = "Authorization 토큰", required = true) @RequestHeader("Authorization")
            String token,
            @RequestBody PostRequestDto request) {

        PostResponseDto response = postService.createPost(request, token);
        return ResponseEntity.ok(BaseResponse.success("포스트 생성 완료", response));
    }

    @Operation(summary = "작품 회차 생성", description = "특정 작품의 회차로 포스트를 생성합니다.")
    @PostMapping("/works/{workId}")
    public ResponseEntity<BaseResponse<PostResponseDto>> createWorkPost(
            @PathVariable Long workId,
            @RequestHeader("Authorization") String token,
            @RequestBody PostRequestDto request) {

        request.setWorkId(workId);
        PostResponseDto response = postService.createPost(request, token);
        return ResponseEntity.ok(BaseResponse.success("작품 회차 포스트 생성 완료", response));
    }

    @Operation(summary = "포스트 목록 조회", description = "포스트 목록을 조회합니다. workId로 필터링 가능합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<PostResponseDto>>> getAllPosts(
            @RequestParam(required = false) Long workId) {

        List<PostResponseDto> list =
                (workId != null) ? postService.getWorkPosts(workId) : postService.getIndependentPosts();

        return ResponseEntity.ok(BaseResponse.success("포스트 목록 조회 완료", list));
    }

    @Operation(summary = "포스트 상세 조회", description = "포스트 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponseDto>> getPostById(@PathVariable Long id) {
        PostResponseDto response = postService.getPostById(id);
        return ResponseEntity.ok(BaseResponse.success("포스트 상세 조회 완료", response));
    }

    @Operation(summary = "임시저장", description = "포스트를 임시저장합니다.")
    @PostMapping("/drafts")
    public ResponseEntity<BaseResponse<PostResponseDto>> saveDraft(
            @RequestHeader("Authorization") String token,
            @RequestBody PostRequestDto request) {

        PostResponseDto response = postService.saveDraft(request, token);
        return ResponseEntity.ok(BaseResponse.success("임시저장 완료", response));
    }

    @Operation(summary = "임시저장 목록 조회", description = "본인의 임시저장 목록을 조회합니다.")
    @GetMapping("/drafts")
    public ResponseEntity<BaseResponse<List<PostResponseDto>>> getAllDrafts(
            @RequestHeader("Authorization") String token) {

        List<PostResponseDto> list = postService.getAllDrafts(token);
        return ResponseEntity.ok(BaseResponse.success("임시저장 목록 조회 완료", list));
    }

    @Operation(summary = "임시저장 단일 조회", description = "임시저장 포스트를 조회합니다.")
    @GetMapping("/drafts/{id}")
    public ResponseEntity<BaseResponse<PostResponseDto>> getDraftById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        PostResponseDto response = postService.getDraftById(id, token);
        return ResponseEntity.ok(BaseResponse.success("임시저장 단일 조회 완료", response));
    }

    @Operation(summary = "포스트 수정", description = "포스트를 수정합니다. 작성자만 수정할 수 있습니다.")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponseDto>> updatePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody PostRequestDto request) {

        PostResponseDto response = postService.updatePost(id, request, token);
        return ResponseEntity.ok(BaseResponse.success("포스트 수정 완료", response));
    }

    @Operation(summary = "포스트 삭제", description = "포스트를 삭제합니다. 작성자만 삭제할 수 있습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        postService.deletePost(id, token);
        return ResponseEntity.ok(BaseResponse.success("포스트 삭제 완료", null));
    }
}
