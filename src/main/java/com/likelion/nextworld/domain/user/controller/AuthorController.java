package com.likelion.nextworld.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.user.service.AuthorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  @GetMapping("/{authorId}/profile")
  public ResponseEntity<?> getProfile(@PathVariable Long authorId) {
    return ResponseEntity.ok(authorService.getAuthorProfile(authorId));
  }

  @GetMapping("/{authorId}/works")
  public ResponseEntity<?> getWorks(@PathVariable Long authorId) {
    return ResponseEntity.ok(authorService.getAuthorWorks(authorId));
  }

  @GetMapping("/{authorId}/posts")
  public ResponseEntity<?> getPosts(@PathVariable Long authorId) {
    return ResponseEntity.ok(authorService.getAuthorPosts(authorId));
  }
}
