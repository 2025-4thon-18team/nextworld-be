package com.likelion.nextworld.domain.user.controller;

import com.likelion.nextworld.domain.user.dto.AuthorPostResponse;
import com.likelion.nextworld.domain.user.dto.AuthorProfileResponse;
import com.likelion.nextworld.domain.user.dto.AuthorWorkResponse;
import com.likelion.nextworld.domain.user.service.AuthorService;
import com.likelion.nextworld.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{authorId}/profile")
    public ResponseEntity<BaseResponse<AuthorProfileResponse>> getProfile(
            @PathVariable Long authorId) {

        AuthorProfileResponse response = authorService.getAuthorProfile(authorId);
        return ResponseEntity.ok(BaseResponse.success("작가 프로필 조회 완료", response));
    }

    @GetMapping("/{authorId}/works")
    public ResponseEntity<BaseResponse<List<AuthorWorkResponse>>> getWorks(
            @PathVariable Long authorId) {

        List<AuthorWorkResponse> response = authorService.getAuthorWorks(authorId);
        return ResponseEntity.ok(BaseResponse.success("작가 작품 목록 조회 완료", response));
    }

    @GetMapping("/{authorId}/posts")
    public ResponseEntity<BaseResponse<List<AuthorPostResponse>>> getPosts(
            @PathVariable Long authorId) {

        List<AuthorPostResponse> response = authorService.getAuthorPosts(authorId);
        return ResponseEntity.ok(BaseResponse.success("작가 포스트 목록 조회 완료", response));
    }
}
