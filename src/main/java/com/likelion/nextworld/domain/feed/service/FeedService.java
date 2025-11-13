package com.likelion.nextworld.domain.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.feed.dto.ListResponse;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

  private final WorkRepository workRepository;
  private final PostRepository postRepository;

  /** 전체 WORK + POST 최신순 조회 - WORK : id 기준 최신순 - POST : createdAt 기준 최신순 */
  public ListResponse getRecentFeed() {

    // 1) Work 리스트 (id DESC)
    List<Work> works = workRepository.findAllByOrderByIdDesc();
    List<WorkResponseDto> workDtos =
        works.stream()
            .map(WorkResponseDto::new) // 생성자 기반 매핑
            .toList();

    // 2) Post 리스트 (createdAt DESC)
    List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
    List<PostResponseDto> postDtos = posts.stream().map(PostResponseDto::new).toList();

    // 3) 한 번에 묶어서 반환
    return ListResponse.builder().works(workDtos).posts(postDtos).build();
  }
}
