package com.likelion.nextworld.domain.like.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.like.dto.LikeResponse;
import com.likelion.nextworld.domain.like.entity.Like;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.user.entity.User;

@Component
public class LikeMapper {

  public Like toEntity(User user, Work work) {
    return Like.builder().user(user).work(work).build();
  }

  public LikeResponse toResponse(Like like) {
    return LikeResponse.builder()
        .id(like.getId())
        .workId(like.getWork().getId())
        .workName(like.getWork().getTitle())
        .createdAt(like.getCreatedAt())
        .build();
  }

  public List<LikeResponse> toResponseList(List<Like> likes) {
    return likes.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
