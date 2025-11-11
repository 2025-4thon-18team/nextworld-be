package com.likelion.nextworld.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkRequestDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkTypeEnum;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkService {

  private final WorkRepository workRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  private User getUserFromToken(String token) {
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
    }

    String actualToken = token.substring(7);
    Long userId = jwtTokenProvider.getUserIdFromToken(actualToken);

    return userRepository
        .findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
  }

  // ✅ 작품 생성
  @Transactional
  public WorkResponseDto createWork(WorkRequestDto req, String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (!jwtTokenProvider.validateToken(token)) {
      throw new RuntimeException("Invalid or expired token");
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(token);
    User author =
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    // workType 필수 검증
    if (req.getWorkType() == null) {
      throw new IllegalArgumentException("작품 타입(workType)은 필수입니다.");
    }

    // 2차 창작물인 경우 parentWorkId 필수 검증
    if (req.getWorkType() == WorkTypeEnum.DERIVATIVE && req.getParentWorkId() == null) {
      throw new IllegalArgumentException("2차 창작물인 경우 원작 작품 ID(parentWorkId)는 필수입니다.");
    }

    // 1차 창작물인 경우 parentWorkId는 NULL이어야 함
    if (req.getWorkType() == WorkTypeEnum.ORIGINAL && req.getParentWorkId() != null) {
      throw new IllegalArgumentException("1차 창작물인 경우 원작 작품 ID(parentWorkId)는 없어야 합니다.");
    }

    Work parentWork = null;
    if (req.getParentWorkId() != null) {
      parentWork =
          workRepository
              .findById(req.getParentWorkId())
              .orElseThrow(() -> new RuntimeException("원작 작품을 찾을 수 없습니다."));
    }

    Work work = new Work();
    work.setWorkType(req.getWorkType());
    work.setParentWork(parentWork);
    work.setTitle(req.getTitle());
    work.setDescription(req.getDescription());
    work.setCoverImageUrl(req.getCoverImageUrl());
    work.setTags(req.getTags()); // 구분자 문자열 그대로 저장
    work.setCategory(req.getCategory());
    work.setSerializationSchedule(req.getSerializationSchedule());
    work.setAllowDerivative(req.getAllowDerivative() != null ? req.getAllowDerivative() : false);
    work.setGuidelineRelation(req.getGuidelineRelation());
    work.setGuidelineContent(req.getGuidelineContent());
    work.setGuidelineBackground(req.getGuidelineBackground());
    work.setBannedWords(req.getBannedWords()); // 구분자 문자열 그대로 저장
    work.setAuthor(author);

    workRepository.save(work);
    return new WorkResponseDto(work);
  }

  // ✅ 작품 목록 조회
  @Transactional(readOnly = true)
  public List<WorkResponseDto> getAllWorks(WorkTypeEnum workType) {
    List<Work> works =
        workType != null ? workRepository.findByWorkType(workType) : workRepository.findAll();

    return works.stream().map(WorkResponseDto::new).collect(Collectors.toList());
  }

  // ✅ 작품 상세 조회
  @Transactional(readOnly = true)
  public WorkResponseDto getWorkById(Long id) {
    Work work =
        workRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

    return new WorkResponseDto(work);
  }

  // ✅ 특정 작품의 회차 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getWorkEpisodes(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다."));

    return work.getEpisodes().stream().map(PostResponseDto::new).collect(Collectors.toList());
  }

  // ✅ 특정 작품의 원작 참조 포스트 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getDerivativePosts(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 원작을 찾을 수 없습니다."));

    // parentWork가 현재 작품인 포스트들을 조회
    return postRepository.findByParentWork(work).stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  // 작품 삭제
  @Transactional
  public void deleteWork(Long id, String token) {
    User currentUser = getUserFromToken(token);
    Work work =
        workRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

    if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
      throw new RuntimeException("작성자만 삭제할 수 있습니다.");
    }

    workRepository.delete(work);
  }
}
