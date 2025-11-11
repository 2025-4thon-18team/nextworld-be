package com.likelion.nextworld.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostStatistics;
import com.likelion.nextworld.domain.post.entity.PostTag;
import com.likelion.nextworld.domain.post.entity.PostType;
import com.likelion.nextworld.domain.post.entity.Tag;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.PostStatisticsRepository;
import com.likelion.nextworld.domain.post.repository.PostTagRepository;
import com.likelion.nextworld.domain.post.repository.TagRepository;
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
  private final PostStatisticsRepository postStatisticsRepository;
  private final PostTagRepository postTagRepository;
  private final TagRepository tagRepository;

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

      // EPISODE인 경우 해당 작품의 작가만 작성 가능
      if (request.getPostType() == PostType.EPISODE) {
        if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
          throw new IllegalStateException("작품의 작가만 회차를 작성할 수 있습니다.");
        }
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
            .status(request.getStatus() != null ? request.getStatus() : WorkStatus.DRAFT)
            .build();

    Post saved = postRepository.save(post);

    // PostStatistics 생성
    PostStatistics statistics =
        PostStatistics.builder()
            .postId(saved.getId())
            .post(saved)
            .viewsCount(0L)
            .commentsCount(0L)
            .build();
    postStatisticsRepository.save(statistics);

    // PostTag 생성
    if (request.getTags() != null && !request.getTags().isEmpty()) {
      for (String tagName : request.getTags()) {
        if (tagName == null || tagName.trim().isEmpty()) {
          continue;
        }
        Tag tag =
            tagRepository
                .findByName(tagName.trim())
                .orElseGet(
                    () -> {
                      Tag newTag = new Tag();
                      newTag.setName(tagName.trim());
                      return tagRepository.save(newTag);
                    });

        PostTag postTag = PostTag.builder().post(saved).tag(tag).build();
        postTagRepository.save(postTag);
      }
    }

    return toPostResponseDto(saved);
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
            .build();

    Post saved = postRepository.save(draft);

    // PostStatistics 생성
    PostStatistics statistics =
        PostStatistics.builder()
            .postId(saved.getId())
            .post(saved)
            .viewsCount(0L)
            .commentsCount(0L)
            .build();
    postStatisticsRepository.save(statistics);

    return toPostResponseDto(saved);
  }

  // DTO 변환
  private PostResponseDto toDto(Post post) {
    return toPostResponseDto(post);
  }

  // 임시저장 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getAllDrafts(String token) {
    User currentUser = getUserFromToken(token);
    return postRepository.findByAuthorAndStatus(currentUser, WorkStatus.DRAFT).stream()
        .map(this::toPostResponseDto)
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
    return toPostResponseDto(draft);
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

    // 기존 Post가 EPISODE인 경우, 작가 검증
    if (post.getPostType() == PostType.EPISODE && post.getWork() != null) {
      if (!post.getWork().getAuthor().getUserId().equals(currentUser.getUserId())) {
        throw new IllegalStateException("작품의 작가만 회차를 수정할 수 있습니다.");
      }
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

      // EPISODE인 경우 해당 작품의 작가만 작성 가능
      PostType postTypeToCheck =
          request.getPostType() != null ? request.getPostType() : post.getPostType();
      if (postTypeToCheck == PostType.EPISODE) {
        if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
          throw new IllegalStateException("작품의 작가만 회차를 작성할 수 있습니다.");
        }
      }

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
      // POST에서 EPISODE로 변경하는 경우 검증
      if (request.getPostType() == PostType.EPISODE && post.getWork() != null) {
        if (!post.getWork().getAuthor().getUserId().equals(currentUser.getUserId())) {
          throw new IllegalStateException("작품의 작가만 회차로 변경할 수 있습니다.");
        }
      }
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

    if (request.getCreationType() != null) {
      post.setCreationType(request.getCreationType());
    }

    // PostTag 업데이트
    if (request.getTags() != null) {
      // 기존 태그 삭제
      postTagRepository.deleteByPost(post);

      // 새 태그 추가
      for (String tagName : request.getTags()) {
        if (tagName == null || tagName.trim().isEmpty()) {
          continue;
        }
        Tag tag =
            tagRepository
                .findByName(tagName.trim())
                .orElseGet(
                    () -> {
                      Tag newTag = new Tag();
                      newTag.setName(tagName.trim());
                      return tagRepository.save(newTag);
                    });

        PostTag postTag = PostTag.builder().post(post).tag(tag).build();
        postTagRepository.save(postTag);
      }
    }

    Post updated = postRepository.save(post);
    return toPostResponseDto(updated);
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
    return toPostResponseDto(post);
  }

  // 작품의 포스트 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getWorkPosts(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다."));
    return postRepository.findByWorkOrderByEpisodeNumberAsc(work).stream()
        .map(this::toPostResponseDto)
        .collect(Collectors.toList());
  }

  // 독립 포스트 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getIndependentPosts() {
    return postRepository.findByWorkIsNull().stream()
        .map(this::toPostResponseDto)
        .collect(Collectors.toList());
  }

  // Post 엔티티 → PostResponseDto 변환
  public PostResponseDto toPostResponseDto(Post post) {
    PostResponseDto dto =
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .hasImage(post.getHasImage())
            .workId(post.getWork() != null ? post.getWork().getId() : null)
            .workTitle(post.getWork() != null ? post.getWork().getTitle() : null)
            .postType(post.getPostType())
            .episodeNumber(post.getEpisodeNumber())
            .parentWorkId(post.getParentWork() != null ? post.getParentWork().getId() : null)
            .parentWorkTitle(post.getParentWork() != null ? post.getParentWork().getTitle() : null)
            .authorName(post.getAuthor() != null ? post.getAuthor().getNickname() : null)
            .creationType(post.getCreationType())
            .isPaid(post.getIsPaid())
            .price(post.getPrice())
            .status(post.getStatus())
            .aiCheck(post.getAiCheck())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .build();

    // PostStatistics 조회
    postStatisticsRepository
        .findById(post.getId())
        .ifPresent(
            statistics -> {
              dto.setViewsCount(statistics.getViewsCount());
              dto.setCommentsCount(statistics.getCommentsCount());
              dto.setRating(statistics.getRating());
            });

    // PostTag 조회
    List<String> tagNames =
        postTagRepository.findByPost(post).stream()
            .map(pt -> pt.getTag().getName())
            .collect(Collectors.toList());
    dto.setTags(tagNames);

    return dto;
  }
}
