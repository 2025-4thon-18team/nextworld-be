package com.likelion.nextworld.domain.post.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.post.dto.PostRatingRequest;
import com.likelion.nextworld.domain.post.dto.PostRatingResponse;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Rating;
import com.likelion.nextworld.domain.post.exception.WorkErrorCode;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.PostStatisticsRepository;
import com.likelion.nextworld.domain.post.repository.RatingRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.exception.UserErrorCode;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

  private final RatingRepository ratingRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostStatisticsRepository postStatisticsRepository;

  private User getCurrentUser(UserPrincipal principal) {
    if (principal == null || principal.getId() == null) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    return userRepository
        .findById(principal.getId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  /** 별점 등록(없으면 생성) / 수정(있으면 업데이트) */
  public PostRatingResponse ratePost(
      Long postId, PostRatingRequest request, UserPrincipal principal) {

    User user = getCurrentUser(principal);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    Rating rating =
        ratingRepository
            .findByUserAndPost(user, post)
            .map(
                existing -> {
                  existing.setScore(request.getScore());
                  return existing;
                })
            .orElseGet(
                () -> Rating.builder().user(user).post(post).score(request.getScore()).build());

    ratingRepository.save(rating);

    BigDecimal avg = ratingRepository.findAverageScoreByPost(post);
    Long count = ratingRepository.countByPost(post);

    postStatisticsRepository
        .findById(post.getId())
        .ifPresent(
            stats -> {
              stats.setRating(avg);
            });

    return PostRatingResponse.builder()
        .postId(post.getId())
        .myScore(request.getScore())
        .averageScore(avg != null ? avg : BigDecimal.ZERO)
        .ratingCount(count != null ? count : 0L)
        .build();
  }

  /** 내가 준 별점 조회 */
  @Transactional(readOnly = true)
  public PostRatingResponse getMyRating(Long postId, UserPrincipal principal) {
    User user = getCurrentUser(principal);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    Rating rating = ratingRepository.findByUserAndPost(user, post).orElse(null);

    BigDecimal avg = ratingRepository.findAverageScoreByPost(post);
    Long count = ratingRepository.countByPost(post);

    return PostRatingResponse.builder()
        .postId(post.getId())
        .myScore(rating != null ? rating.getScore() : null)
        .averageScore(avg != null ? avg : BigDecimal.ZERO)
        .ratingCount(count != null ? count : 0L)
        .build();
  }

  /** 해당 포스트의 별점 요약(평균 + 카운트) */
  @Transactional(readOnly = true)
  public PostRatingResponse getRatingSummary(Long postId) {

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    BigDecimal avg = ratingRepository.findAverageScoreByPost(post);
    Long count = ratingRepository.countByPost(post);

    return PostRatingResponse.builder()
        .postId(post.getId())
        .myScore(null)
        .averageScore(avg != null ? avg : BigDecimal.ZERO)
        .ratingCount(count != null ? count : 0L)
        .build();
  }
}
