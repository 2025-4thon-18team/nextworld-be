package com.likelion.nextworld.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkRequest;
import com.likelion.nextworld.domain.post.dto.WorkResponse;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkService {

  private final WorkRepository workRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  private User getUserFromToken(String token) {
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
    }

    String actualToken = token.substring(7);
    String email = jwtTokenProvider.getEmailFromToken(actualToken);

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
  }

  // ✅ 1차 창작물 생성
  public WorkResponse createWork(WorkRequest req, String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (!jwtTokenProvider.validateToken(token)) {
      throw new RuntimeException("Invalid or expired token");
    }

    String email = jwtTokenProvider.getEmailFromToken(token);
    User author =
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    Work work = new Work();
    work.setTitle(req.getTitle());
    work.setDescription(req.getDescription());
    work.setCoverImageUrl(req.getCoverImageUrl());
    work.setTags(req.getTags());
    work.setUniverseDescription(req.getUniverseDescription());
    work.setAllowDerivative(req.getAllowDerivative());
    work.setGuidelineRelation(req.getGuidelineRelation());
    work.setGuidelineContent(req.getGuidelineContent());
    work.setGuidelineBackground(req.getGuidelineBackground());
    work.setBannedWords(req.getBannedWords());
    work.setIsPaid(req.getIsPaid());
    work.setAllowDerivativeProfit(req.getAllowDerivativeProfit());
    work.setAuthor(author);

    workRepository.save(work);
    return new WorkResponse(work);
  }

  // ✅ 특정 1차 창작물의 2차 작품 목록 조회
  public List<PostResponseDto> getDerivativePosts(Long workId) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new RuntimeException("해당 원작을 찾을 수 없습니다."));

    return work.getDerivativePosts().stream()
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
