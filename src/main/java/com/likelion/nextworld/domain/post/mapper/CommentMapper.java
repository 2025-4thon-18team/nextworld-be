package com.likelion.nextworld.domain.post.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.post.dto.CommentResponse;
import com.likelion.nextworld.domain.post.entity.Comment;

@Component
public class CommentMapper {

  public CommentResponse toResponse(Comment c) {
    return CommentResponse.builder()
        .id(c.getId())
        .postId(c.getPost().getId())
        .parentCommentId(c.getParent() == null ? null : c.getParent().getId())
        .authorId(c.getAuthor().getUserId())
        .authorName(c.getAuthor().getName())
        .authorImageUrl(c.getAuthor().getProfileImageUrl())
        .content(c.getContent())
        .createdAt(c.getCreatedAt())
        .updatedAt(c.getUpdatedAt())
        .build();
  }

  public List<CommentResponse> toResponseList(List<Comment> comments) {
    return comments.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
