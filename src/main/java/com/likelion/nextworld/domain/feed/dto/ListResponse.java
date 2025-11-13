package com.likelion.nextworld.domain.feed.dto;

import java.util.List;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListResponse {

  private List<WorkResponseDto> works;
  private List<PostResponseDto> posts;
}
