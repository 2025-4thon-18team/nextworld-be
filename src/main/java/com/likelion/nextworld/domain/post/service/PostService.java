package com.likelion.nextworld.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostRequestDto;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
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
  private final WorkRepository workRepository; //  추가
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

  // 2차 창작물 등록
  @Transactional
  public PostResponseDto createWork(PostRequestDto request, String token) {
    User currentUser = getUserFromToken(token);

    Work parentWork = null;
    if (request.getParentId() != null) {
      parentWork =
          workRepository
              .findById(request.getParentId())
              .orElseThrow(() -> new RuntimeException("원작을 찾을 수 없습니다."));

      // ✅ 2차 창작 허용 여부 검사 추가
      if (Boolean.FALSE.equals(parentWork.getAllowDerivative())) {
        throw new RuntimeException("이 작품은 2차 창작이 허용되지 않았습니다.");
      }
    }

    Post work =
        Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .author(currentUser)
            .status(request.getStatus())
            .workType(request.getWorkType())
            .creationType(request.getCreationType())
            .parentWork(parentWork)
            .build();

    Post saved = postRepository.save(work);
    return toDto(saved);
  }

  // 임시저장
  @Transactional
  public PostResponseDto saveDraft(PostRequestDto request, String token) {
    User currentUser = getUserFromToken(token);

    Post draft =
        Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .author(currentUser)
            .status(WorkStatus.DRAFT)
            .workType(request.getWorkType())
            .creationType(request.getCreationType())
            .build();

    Post saved = postRepository.save(draft);
    return toDto(saved);
  }

  // DTO 변환
  private PostResponseDto toDto(Post work) {
    return PostResponseDto.builder()
        .id(work.getId())
        .title(work.getTitle())
        .content(work.getContent())
        .authorName(work.getAuthor() != null ? work.getAuthor().getNickname() : null)
        .workTitle(work.getParentWork() != null ? work.getParentWork().getTitle() : null)
        .status(work.getStatus())
        .workType(work.getWorkType())
        .creationType(work.getCreationType())
        .createdAt(work.getCreatedAt())
        .updatedAt(work.getUpdatedAt())
        .build();
  }

  //  임시저장 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getAllDrafts(String token) {
    User currentUser = getUserFromToken(token);
    return postRepository.findByAuthorAndStatus(currentUser, WorkStatus.DRAFT).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  //  단일 임시저장 조회
  @Transactional(readOnly = true)
  public PostResponseDto getDraftById(Long id, String token) {
    User currentUser = getUserFromToken(token);
    Post draft =
        postRepository
            .findByIdAndAuthorAndStatus(id, currentUser, WorkStatus.DRAFT)
            .orElseThrow(() -> new RuntimeException("본인의 임시저장 글이 아니거나 존재하지 않습니다."));
    return toDto(draft);
  }

  //  작품 수정
  @Transactional
  public PostResponseDto updateWork(Long id, PostRequestDto request, String token) {
    User currentUser = getUserFromToken(token);
    Post work =
        postRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

    if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
      throw new RuntimeException("작성자만 수정할 수 있습니다.");
    }

    work.setTitle(request.getTitle());
    work.setContent(request.getContent());

    if (request.getStatus() != null) {
      work.setStatus(request.getStatus());
    }

    Post updated = postRepository.save(work);
    return toDto(updated);
  }

  // 작품 삭제
  @Transactional
  public void deleteWork(Long id, String token) {
    User currentUser = getUserFromToken(token);
    Post work =
        postRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

    if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
      throw new RuntimeException("작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(work);
  }
}
