package com.likelion.nextworld.domain.user.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.post.entity.*;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.PostStatisticsRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.post.repository.WorkStatisticsRepository;
import com.likelion.nextworld.domain.user.dto.AuthorPostResponse;
import com.likelion.nextworld.domain.user.dto.AuthorProfileResponse;
import com.likelion.nextworld.domain.user.dto.AuthorWorkResponse;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {

  private final UserRepository userRepository;
  private final WorkRepository workRepository;
  private final PostRepository postRepository;
  private final WorkStatisticsRepository workStatisticsRepository;
  private final PostStatisticsRepository postStatisticsRepository;

  public AuthorProfileResponse getAuthorProfile(Long authorId) {
    User author =
        userRepository
            .findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("작가를 찾을 수 없습니다."));

    return AuthorProfileResponse.builder()
        .authorId(author.getUserId())
        .nickname(author.getNickname())
        .bio(author.getBio())
        .contactEmail(author.getContactEmail())
        .twitter(author.getTwitter())
        .profileImageUrl(author.getProfileImageUrl())
        .build();
  }

  public List<AuthorWorkResponse> getAuthorWorks(Long authorId) {

    List<Work> works = workRepository.findByAuthorUserId(authorId);

    return works.stream()
        .map(
            work -> {
              List<String> tags =
                  work.getTags().stream().map(tag -> tag.getTag().getName()).toList();

              Long parentWorkId = null;
              String parentWorkTitle = null;
              if (work.getParentWork() != null) {
                parentWorkId = work.getParentWork().getId();
                parentWorkTitle = work.getParentWork().getTitle();
              }

              WorkStatistics stats = workStatisticsRepository.findById(work.getId()).orElse(null);

              Long totalLikesCount = 0L;
              Long totalViewsCount = 0L;
              BigDecimal totalRating = null;

              if (stats != null) {
                totalLikesCount = stats.getTotalLikesCount();
                totalViewsCount = stats.getTotalViewsCount();
                totalRating = stats.getTotalRating();
              }

              return AuthorWorkResponse.builder()
                  .id(work.getId())
                  .workType(work.getWorkType())
                  .title(work.getTitle())
                  .description(work.getDescription())
                  .coverImageUrl(work.getCoverImageUrl())
                  .category(work.getCategory())
                  .serializationSchedule(work.getSerializationSchedule())
                  .allowDerivative(work.getAllowDerivative())
                  .tags(tags)
                  .totalLikesCount(totalLikesCount)
                  .totalViewsCount(totalViewsCount)
                  .totalRating(totalRating)
                  .authorName(work.getAuthor().getNickname())
                  .parentWorkId(parentWorkId)
                  .parentWorkTitle(parentWorkTitle)
                  .build();
            })
        .toList();
  }

  public List<AuthorPostResponse> getAuthorPosts(Long authorId) {

    User author =
        userRepository
            .findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("작가를 찾을 수 없습니다."));

    List<Post> posts = postRepository.findByAuthorAndStatus(author, WorkStatus.PUBLISHED);

    return posts.stream()
        .map(
            post -> {
              List<String> tags =
                  post.getTags().stream().map(tag -> tag.getTag().getName()).toList();

              Long workId = post.getWork() != null ? post.getWork().getId() : null;
              String workTitle = post.getWork() != null ? post.getWork().getTitle() : null;

              Long parentWorkId = null;
              String parentWorkTitle = null;

              if (post.getWork() != null && post.getWork().getParentWork() != null) {
                parentWorkId = post.getWork().getParentWork().getId();
                parentWorkTitle = post.getWork().getParentWork().getTitle();
              }

              PostStatistics stats = postStatisticsRepository.findById(post.getId()).orElse(null);

              Long viewsCount = 0L;
              Long commentsCount = 0L;
              BigDecimal rating = null;

              if (stats != null) {
                viewsCount = stats.getViewsCount();
                commentsCount = stats.getCommentsCount();
                rating = stats.getRating();
              }

              return AuthorPostResponse.builder()
                  .id(post.getId())
                  .title(post.getTitle())
                  .hasImage(post.getHasImage())
                  .workId(workId)
                  .workTitle(workTitle)
                  .postType(post.getPostType())
                  .episodeNumber(post.getEpisodeNumber())
                  .parentWorkId(parentWorkId)
                  .parentWorkTitle(parentWorkTitle)
                  .authorName(post.getAuthor().getNickname())
                  .creationType(post.getCreationType())
                  .isPaid(post.getIsPaid())
                  .price(post.getPrice())
                  .tags(tags)
                  .viewsCount(viewsCount)
                  .commentsCount(commentsCount)
                  .rating(rating)
                  .status(post.getStatus())
                  .aiCheck(post.getAiCheck())
                  .createdAt(post.getCreatedAt())
                  .updatedAt(post.getUpdatedAt())
                  .build();
            })
        .toList();
  }
}
