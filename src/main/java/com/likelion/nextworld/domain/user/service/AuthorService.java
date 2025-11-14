package com.likelion.nextworld.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
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
                  work.getTags().stream().map(workTag -> workTag.getTag().getName()).toList();

              return AuthorWorkResponse.builder()
                  .workId(work.getId())
                  .title(work.getTitle())
                  .coverImageUrl(work.getCoverImageUrl())
                  .category(work.getCategory())
                  .workType(work.getWorkType().name())
                  .tags(tags)
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
              String thumbnail = post.getWork() != null ? post.getWork().getCoverImageUrl() : null;

              List<String> tags =
                  post.getTags().stream().map(postTag -> postTag.getTag().getName()).toList();

              return AuthorPostResponse.builder()
                  .postId(post.getId())
                  .workId(post.getWork() != null ? post.getWork().getId() : null)
                  .title(post.getTitle())
                  .thumbnailUrl(thumbnail)
                  .tags(tags)
                  .build();
            })
        .toList();
  }
}
