package com.likelion.nextworld.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostType;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final WorkRepository workRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  // JWT 토큰에서 사용자 정보 추출
  private User getUserFromToken(String token) {
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
    }
    String actualToken = token.substring(7);
    Long userId = jwtTokenProvider.getUserIdFromToken(actualToken);

    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }

  // 포스트 생성 (독립 포스트 또는 작품 회차)
  @Transactional
  public PostResponseDto createPost(PostRequestDto request, String token) {
    User currentUser = getUserFromToken(token);

    Work work = null;
    Work parentWork = null;

    // workId가 있으면 작품 회차
    if (request.getWorkId() != null) {
      work =
          workRepository
              .findById(request.getWorkId())
              .orElseThrow(() -> new RuntimeException("소속 작품을 찾을 수 없습니다."));

      // 작품 회차인 경우 postType은 EPISODE여야 함
      if (request.getPostType() == null) {
        request.setPostType(PostType.EPISODE);
      }
    }

    // parentWorkId가 있으면 원작 참조
    if (request.getParentWorkId() != null) {
      parentWork =
          workRepository
              .findById(request.getParentWorkId())
              .orElseThrow(() -> new RuntimeException("원작 작품을 찾을 수 없습니다."));
    }

    // workId 또는 parentWorkId 중 하나는 반드시 지정되어야 함
    if (work == null && parentWork == null) {
      throw new IllegalArgumentException("workId 또는 parentWorkId 중 하나는 필수입니다.");
    }

    Post post =
        Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .hasImage(request.getHasImage() != null ? request.getHasImage() : false)
            .work(work)
            .postType(request.getPostType() != null ? request.getPostType() : PostType.POST)
            .episodeNumber(request.getEpisodeNumber())
            .parentWork(parentWork)
            .creationType(request.getCreationType())
            .author(currentUser)
            .isPaid(request.getIsPaid() != null ? request.getIsPaid() : false)
            .price(request.getPrice())
            .tags(request.getTags())
            .status(request.getStatus() != null ? request.getStatus() : WorkStatus.DRAFT)
            .build();

    Post saved = postRepository.save(post);

    // 작품 통계 업데이트 (나중에 구현)
    // updateWorkStatistics(work);

    return new PostResponseDto(saved);
  }

  // 임시저장
  @Transactional
  public PostResponseDto saveDraft(PostRequestDto request, String token) {
    User currentUser = getUserFromToken(token);

    Post draft =
        Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .hasImage(request.getHasImage() != null ? request.getHasImage() : false)
            .author(currentUser)
            .status(WorkStatus.DRAFT)
            .postType(request.getPostType() != null ? request.getPostType() : PostType.POST)
            .creationType(request.getCreationType())
            .tags(request.getTags())
            .build();

    Post saved = postRepository.save(draft);
    return new PostResponseDto(saved);
  }

  // DTO 변환
  private PostResponseDto toDto(Post post) {
    return new PostResponseDto(post);
  }

  // 임시저장 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getAllDrafts(String token) {
    User currentUser = getUserFromToken(token);
    return postRepository.findByAuthorAndStatus(currentUser, WorkStatus.DRAFT).stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  // 단일 임시저장 조회
  @Transactional(readOnly = true)
  public PostResponseDto getDraftById(Long id, String token) {
    User currentUser = getUserFromToken(token);
    Post draft =
        postRepository
            .findByIdAndAuthorAndStatus(id, currentUser, WorkStatus.DRAFT)
            .orElseThrow(() -> new RuntimeException("본인의 임시저장 글이 아니거나 존재하지 않습니다."));
    return new PostResponseDto(draft);
  }

  // 포스트 수정
  @Transactional
  public PostResponseDto updatePost(Long id, PostRequestDto request, String token) {
    User currentUser = getUserFromToken(token);
    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 포스트를 찾을 수 없습니다. ID: " + id));

    if (!post.getAuthor().getUserId().equals(currentUser.getUserId())) {
      throw new RuntimeException("작성자만 수정할 수 있습니다.");
    }

    post.setTitle(request.getTitle());
    post.setContent(request.getContent());
    if (request.getHasImage() != null) {
      post.setHasImage(request.getHasImage());
    }

    // workId 업데이트
    if (request.getWorkId() != null) {
      Work work =
          workRepository
              .findById(request.getWorkId())
              .orElseThrow(() -> new RuntimeException("소속 작품을 찾을 수 없습니다."));
      post.setWork(work);
      // 작품 회차인 경우 postType은 EPISODE여야 함
      if (request.getPostType() == null) {
        post.setPostType(PostType.EPISODE);
      }
    }

    // parentWorkId 업데이트
    if (request.getParentWorkId() != null) {
      Work parentWork =
          workRepository
              .findById(request.getParentWorkId())
              .orElseThrow(() -> new RuntimeException("원작 작품을 찾을 수 없습니다."));
      post.setParentWork(parentWork);
    }

    // workId 또는 parentWorkId 중 하나는 반드시 지정되어야 함
    if (post.getWork() == null && post.getParentWork() == null) {
      throw new IllegalArgumentException("workId 또는 parentWorkId 중 하나는 필수입니다.");
    }

    if (request.getPostType() != null) {
      post.setPostType(request.getPostType());
    }

    if (request.getEpisodeNumber() != null) {
      post.setEpisodeNumber(request.getEpisodeNumber());
    }

    if (request.getStatus() != null) {
      post.setStatus(request.getStatus());
    }

    if (request.getIsPaid() != null) {
      post.setIsPaid(request.getIsPaid());
    }

    if (request.getPrice() != null) {
      post.setPrice(request.getPrice());
    }

    if (request.getTags() != null) {
      post.setTags(request.getTags());
    }

    if (request.getCreationType() != null) {
      post.setCreationType(request.getCreationType());
    }

    Post updated = postRepository.save(post);
    return new PostResponseDto(updated);
  }

  // 포스트 삭제
  @Transactional
  public void deletePost(Long id, String token) {
    User currentUser = getUserFromToken(token);
    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 포스트를 찾을 수 없습니다. ID: " + id));

    if (!post.getAuthor().getUserId().equals(currentUser.getUserId())) {
      throw new RuntimeException("작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
  }

  // 포스트 상세 조회
  @Transactional(readOnly = true)
  public PostResponseDto getPostById(Long id) {
    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 포스트를 찾을 수 없습니다. ID: " + id));
    return new PostResponseDto(post);
  }

  // 작품의 포스트 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getWorkPosts(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다."));
    return postRepository.findByWorkOrderByEpisodeNumberAsc(work).stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  // 독립 포스트 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getIndependentPosts() {
    return postRepository.findByWorkIsNull().stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }
}
