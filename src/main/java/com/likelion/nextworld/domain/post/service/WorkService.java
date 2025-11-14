package com.likelion.nextworld.domain.post.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkGuidelineResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkRequestDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.*;
import com.likelion.nextworld.domain.post.repository.*;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.JwtTokenProvider;
import com.likelion.nextworld.global.service.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkService {

  private final WorkRepository workRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final WorkGuidelineRepository workGuidelineRepository;
  private final WorkStatisticsRepository workStatisticsRepository;
  private final WorkTagRepository workTagRepository;
  private final TagRepository tagRepository;
  private final PostService postService;
  private final S3Uploader s3Uploader;

  // 이미지 업로드 전용
  @Transactional
  public String uploadImage(String token, MultipartFile file) throws IOException {
    if (token.startsWith("Bearer ")) token = token.substring(7);
    if (!jwtTokenProvider.validateToken(token))
      throw new RuntimeException("Invalid or expired token");

    // S3 업로드
    return s3Uploader.upload(file, "work");
  }

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
    work.setCategory(req.getCategory());
    work.setSerializationSchedule(req.getSerializationSchedule());
    work.setAllowDerivative(req.getAllowDerivative() != null ? req.getAllowDerivative() : false);
    work.setAuthor(author);

    Work savedWork = workRepository.save(work);

    // WorkGuideline 생성
    if (req.getGuidelineRelation() != null
        || req.getGuidelineContent() != null
        || req.getGuidelineBackground() != null
        || req.getBannedWords() != null) {
      WorkGuideline guideline =
          WorkGuideline.builder()
              .work(savedWork) // 이것만 넣으면 OK
              .guidelineRelation(req.getGuidelineRelation())
              .guidelineContent(req.getGuidelineContent())
              .guidelineBackground(req.getGuidelineBackground())
              .word(req.getBannedWords())
              .build();
      workGuidelineRepository.save(guideline);
    }

    // WorkStatistics 생성
    WorkStatistics statistics =
        WorkStatistics.builder()
            .work(savedWork) // PK는 Hibernate가 자동으로 work.id로 채워줌
            .totalLikesCount(0L)
            .totalViewsCount(0L)
            .build();

    workStatisticsRepository.save(statistics);

    // WorkTag 생성
    if (req.getTags() != null && !req.getTags().isEmpty()) {
      for (String tagName : req.getTags()) {
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

        WorkTag workTag = WorkTag.builder().work(savedWork).tag(tag).build();
        workTagRepository.save(workTag);
      }
    }

    return toWorkResponseDto(savedWork);
  }

  // ✅ 작품 목록 조회
  @Transactional(readOnly = true)
  public List<WorkResponseDto> getAllWorks(WorkTypeEnum workType) {
    List<Work> works =
        workType != null ? workRepository.findByWorkType(workType) : workRepository.findAll();

    return works.stream().map(this::toWorkResponseDto).collect(Collectors.toList());
  }

  // ✅ 작품 상세 조회
  @Transactional(readOnly = true)
  public WorkResponseDto getWorkById(Long id) {
    Work work =
        workRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

    return toWorkResponseDto(work);
  }

  // ✅ 특정 작품의 회차 목록 조회
  @Transactional(readOnly = true)
  public List<PostResponseDto> getWorkEpisodes(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다."));

    return work.getEpisodes().stream()
        .map(post -> postService.toPostResponseDto(post))
        .collect(Collectors.toList());
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
        .map(post -> postService.toPostResponseDto(post))
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

  // Work 엔티티 → WorkResponseDto 변환
  public WorkResponseDto toWorkResponseDto(Work work) {
    WorkResponseDto dto = new WorkResponseDto();
    dto.setId(work.getId());
    dto.setWorkType(work.getWorkType());
    dto.setTitle(work.getTitle());
    dto.setDescription(work.getDescription());
    dto.setCoverImageUrl(work.getCoverImageUrl());
    dto.setCategory(work.getCategory());
    dto.setSerializationSchedule(work.getSerializationSchedule());
    dto.setAllowDerivative(work.getAllowDerivative());
    dto.setAuthorName(work.getAuthor() != null ? work.getAuthor().getNickname() : null);
    dto.setParentWorkId(work.getParentWork() != null ? work.getParentWork().getId() : null);
    dto.setParentWorkTitle(work.getParentWork() != null ? work.getParentWork().getTitle() : null);

    // WorkStatistics 조회
    workStatisticsRepository
        .findById(work.getId())
        .ifPresent(
            statistics -> {
              dto.setTotalLikesCount(statistics.getTotalLikesCount());
              dto.setTotalViewsCount(statistics.getTotalViewsCount());
              dto.setTotalRating(statistics.getTotalRating());
            });

    // WorkTag 조회
    List<String> tagNames =
        workTagRepository.findByWork(work).stream()
            .map(wt -> wt.getTag().getName())
            .collect(Collectors.toList());
    dto.setTags(tagNames);

    return dto;
  }

  // 작품 가이드라인 조회
  @Transactional(readOnly = true)
  public WorkGuidelineResponseDto getWorkGuideline(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + workId));

    return workGuidelineRepository
        .findById(workId)
        .map(
            guideline ->
                WorkGuidelineResponseDto.builder()
                    .workId(work.getId())
                    .workTitle(work.getTitle())
                    .guidelineRelation(guideline.getGuidelineRelation())
                    .guidelineContent(guideline.getGuidelineContent())
                    .guidelineBackground(guideline.getGuidelineBackground())
                    .bannedWords(guideline.getWord())
                    .build())
        .orElseThrow(() -> new RuntimeException("해당 작품의 가이드라인을 찾을 수 없습니다."));
  }
}
