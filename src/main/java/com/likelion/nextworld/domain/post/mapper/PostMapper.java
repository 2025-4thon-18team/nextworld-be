package com.likelion.nextworld.domain.post.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.repository.PostStatisticsRepository;
import com.likelion.nextworld.domain.post.repository.PostTagRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

  private final PostStatisticsRepository postStatisticsRepository;
  private final PostTagRepository postTagRepository;

  public PostResponseDto toDto(Post post) {
    if (post == null) {
      return null;
    }

    PostResponseDto dto =
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .hasImage(post.getHasImage())

            // 소속 작품
            .workId(post.getWork() != null ? post.getWork().getId() : null)
            .workTitle(post.getWork() != null ? post.getWork().getTitle() : null)
            .postType(post.getPostType())
            .episodeNumber(post.getEpisodeNumber())

            // 원작 작품
            .parentWorkId(post.getParentWork() != null ? post.getParentWork().getId() : null)
            .parentWorkTitle(post.getParentWork() != null ? post.getParentWork().getTitle() : null)

            // 작성자
            .authorName(post.getAuthor() != null ? post.getAuthor().getNickname() : null)
            .creationType(post.getCreationType())
            .isPaid(post.getIsPaid())
            .price(post.getPrice())
            .status(post.getStatus())
            .aiCheck(post.getAiCheck())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .build();

    postStatisticsRepository
        .findById(post.getId())
        .ifPresent(
            statistics -> {
              dto.setViewsCount(statistics.getViewsCount());
              dto.setCommentsCount(statistics.getCommentsCount());
              dto.setRating(statistics.getRating());
            });

    List<String> tagNames =
        postTagRepository.findByPost(post).stream()
            .map(pt -> pt.getTag().getName())
            .collect(Collectors.toList());
    dto.setTags(tagNames);

    return dto;
  }

  public List<PostResponseDto> toDtoList(List<Post> posts) {
    if (posts == null || posts.isEmpty()) {
      return List.of();
    }
    return posts.stream().map(this::toDto).toList();
  }
}
