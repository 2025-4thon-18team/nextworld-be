package com.likelion.nextworld.domain.post.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostTag;

@Component
public class PostMapper {

  public PostResponseDto toDto(Post post) {
    if (post == null) {
      return null;
    }

    // 태그 문자열 리스트(PostTag -> Tag -> name)
    List<String> tags =
        post.getTags() != null
            ? post.getTags().stream().map(PostTag::getTag).map(tag -> tag.getName()).toList()
            : List.of();

    return PostResponseDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .hasImage(post.getHasImage())

        // 소속 작품
        .workId(post.getWork() != null ? post.getWork().getId() : null)
        .workTitle(post.getWork() != null ? post.getWork().getTitle() : null)
        .postType(post.getPostType())
        .episodeNumber(post.getEpisodeNumber())

        // 부모(원작) 작품
        .parentWorkId(post.getParentWork() != null ? post.getParentWork().getId() : null)
        .parentWorkTitle(post.getParentWork() != null ? post.getParentWork().getTitle() : null)

        // 작성자
        .authorName(post.getAuthor() != null ? post.getAuthor().getNickname() : null)
        .creationType(post.getCreationType())
        .isPaid(post.getIsPaid())
        .price(post.getPrice())

        // 태그
        .tags(tags)

        // 통계 (PostStatistics 없으므로 기본값)
        .viewsCount(0L)
        .commentsCount(0L)
        .rating(null)
        .status(post.getStatus())
        .aiCheck(post.getAiCheck())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }

  public List<PostResponseDto> toDtoList(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toDto).toList();
  }
}
