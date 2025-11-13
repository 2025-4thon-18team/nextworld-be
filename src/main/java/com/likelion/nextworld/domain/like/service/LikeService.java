package com.likelion.nextworld.domain.like.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.like.dto.LikeResponse;
import com.likelion.nextworld.domain.like.entity.Like;
import com.likelion.nextworld.domain.like.exception.LikeErrorCode;
import com.likelion.nextworld.domain.like.mapper.LikeMapper;
import com.likelion.nextworld.domain.like.repository.LikeRepository;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.exception.WorkErrorCode;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.exception.UserErrorCode;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

  private final LikeRepository likeRepository;
  private final LikeMapper likeMapper;
  private final UserRepository userRepository;
  private final WorkRepository workRepository;

  private User getCurrentUser(UserPrincipal principal) {
    if (principal == null || principal.getId() == null) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    return userRepository
        .findById(principal.getId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  /** 좋아요 생성 */
  public LikeResponse createLike(Long workId, UserPrincipal principal) {
    User user = getCurrentUser(principal);

    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    if (likeRepository.existsByUserAndWork(user, work)) {
      throw new CustomException(LikeErrorCode.ALREADY_LIKED);
    }

    Like like = likeMapper.toEntity(user, work);
    Like saved = likeRepository.save(like);
    return likeMapper.toResponse(saved);
  }

  /** 좋아요 취소 */
  public void deleteLike(Long workId, UserPrincipal principal) {
    User user = getCurrentUser(principal);

    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    Like like =
        likeRepository
            .findByUserAndWork(user, work)
            .orElseThrow(() -> new CustomException(LikeErrorCode.LIKE_NOT_FOUND));

    likeRepository.delete(like);
  }

  public List<Like> getMyLikeEntities(UserPrincipal principal) {
    User user = getCurrentUser(principal);
    return likeRepository.findAllByUser(user);
  }
}
