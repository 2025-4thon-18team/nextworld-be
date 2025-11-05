package com.likelion.nextworld.domain.post.dto;

import java.time.LocalDateTime;

import com.likelion.nextworld.domain.post.entity.CreationType;
import com.likelion.nextworld.domain.post.entity.DerivativeWork;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.entity.WorkType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivativeWorkResponseDto {
  private Long id;
  private String title;
  private String content;
  private String authorName;
  private WorkStatus status;
  private WorkType workType; // 단편 / 장편
  private CreationType creationType; // 오리지널 / 2차창작
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static DerivativeWorkResponseDto from(DerivativeWork work) {
    return DerivativeWorkResponseDto.builder()
        .id(work.getId())
        .title(work.getTitle())
        .content(work.getContent())
        .authorName(work.getAuthor() != null ? work.getAuthor().getNickname() : null)
        .status(work.getStatus())
        .workType(work.getWorkType())
        .creationType(work.getCreationType())
        .createdAt(work.getCreatedAt())
        .updatedAt(work.getUpdatedAt())
        .build();
  }
}
