package com.likelion.nextworld.domain.search.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.search.dto.SearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

  private final WorkRepository workRepository;
  private final PostRepository postRepository;

  public SearchResponse search(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      // 검색어가 비어있으면 그냥 빈 리스트 반환
      return SearchResponse.builder()
          .works(Collections.emptyList())
          .posts(Collections.emptyList())
          .build();
    }

    String q = keyword.trim();

    // 1) Work 검색 (제목 + 설명 기준)
    List<Work> works =
        workRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q);

    List<WorkResponseDto> workDtos = works.stream().map(WorkResponseDto::new).toList();

    // 2) Post 검색 (제목 + 내용 기준)
    List<Post> posts =
        postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q);

    List<PostResponseDto> postDtos = posts.stream().map(PostResponseDto::new).toList();

    return SearchResponse.builder().works(workDtos).posts(postDtos).build();
  }
}
