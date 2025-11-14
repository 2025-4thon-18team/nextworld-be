package com.likelion.nextworld.domain.post.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkTag;
import com.likelion.nextworld.domain.post.repository.WorkStatisticsRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkMapper {

  private final WorkStatisticsRepository workStatisticsRepository;

  public WorkResponseDto toDto(Work work) {
    if (work == null) {
      return null;
    }

    // 태그
    List<String> tags =
        work.getTags() != null
            ? work.getTags().stream().map(WorkTag::getTag).map(tag -> tag.getName()).toList()
            : List.of();

    var statsOpt = workStatisticsRepository.findByWork(work);
    Long totalLikesCount = null;
    Long totalViewsCount = null;
    BigDecimal totalRating = null;

    if (statsOpt.isPresent()) {
      var stats = statsOpt.get();
      totalLikesCount = stats.getTotalLikesCount() != null ? stats.getTotalLikesCount() : 0L;
      totalViewsCount = stats.getTotalViewsCount() != null ? stats.getTotalViewsCount() : 0L;
      totalRating = stats.getTotalRating(); // 상세 조회처럼 null 유지
    } else {
      // 통계 레코드가 없으면 기본값
      totalLikesCount = 0L;
      totalViewsCount = 0L;
      totalRating = null;
    }

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
