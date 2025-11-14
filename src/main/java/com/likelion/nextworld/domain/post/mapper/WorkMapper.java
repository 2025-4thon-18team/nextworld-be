package com.likelion.nextworld.domain.post.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkTag;

@Component
public class WorkMapper {

  public WorkResponseDto toDto(Work work) {
    if (work == null) {
      return null;
    }

    // 태그 문자열 리스트로 변환 (WorkTag -> Tag -> name)
    List<String> tags =
        work.getTags() != null
            ? work.getTags().stream().map(WorkTag::getTag).map(tag -> tag.getName()).toList()
            : List.of();

    // 통계 필드는 아직 Work 엔티티에 없으니까 기본값/NULL로 세팅
    Long totalLikesCount = 0L;
    Long totalViewsCount = 0L;
    BigDecimal totalRating = BigDecimal.ZERO;

    return WorkResponseDto.builder()
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
        .authorName(work.getAuthor() != null ? work.getAuthor().getNickname() : null)
        .parentWorkId(work.getParentWork() != null ? work.getParentWork().getId() : null)
        .parentWorkTitle(work.getParentWork() != null ? work.getParentWork().getTitle() : null)
        .build();
  }

  public List<WorkResponseDto> toDtoList(List<Work> works) {
    if (works == null) {
      return List.of();
    }
    return works.stream().map(this::toDto).toList();
  }
}
