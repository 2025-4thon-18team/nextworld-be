package com.likelion.nextworld.domain.scrap.controller;

import com.likelion.nextworld.domain.scrap.dto.ScrapResponse;
import com.likelion.nextworld.domain.scrap.service.ScrapService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scraps")
@Tag(name = "Scrap", description = "스크랩(북마크) 관련 API")
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(summary = "WORK 스크랩 등록", description = "특정 workId를 스크랩으로 등록합니다. (로그인 필요)")
    @PostMapping("/works/{workId}")
    public ResponseEntity<BaseResponse<ScrapResponse>> scrapWork(
            @PathVariable Long workId, @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(
                BaseResponse.success("스크랩 등록 완료", scrapService.createWorkScrap(workId, user)));
    }

    @Operation(summary = "POST 스크랩 등록", description = "특정 postId를 스크랩으로 등록합니다. (로그인 필요)")
    @PostMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<ScrapResponse>> scrapPost(
            @PathVariable Long postId, @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(
                BaseResponse.success("스크랩 등록 완료", scrapService.createPostScrap(postId, user)));
    }

    @Operation(summary = "WORK 스크랩 삭제", description = "특정 workId의 스크랩을 삭제합니다. (로그인 필요)")
    @DeleteMapping("/works/{workId}")
    public ResponseEntity<BaseResponse<String>> unscrapWork(
            @PathVariable Long workId, @AuthenticationPrincipal UserPrincipal user) {
        scrapService.deleteWorkScrap(workId, user);
        return ResponseEntity.ok(BaseResponse.success("스크랩 취소 완료", null));
    }

    @Operation(summary = "POST 스크랩 삭제", description = "특정 postId의 스크랩을 삭제합니다. (로그인 필요)")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<String>> unscrapPost(
            @PathVariable Long postId, @AuthenticationPrincipal UserPrincipal user) {
        scrapService.deletePostScrap(postId, user);
        return ResponseEntity.ok(BaseResponse.success("스크랩 취소 완료", null));
    }
}
