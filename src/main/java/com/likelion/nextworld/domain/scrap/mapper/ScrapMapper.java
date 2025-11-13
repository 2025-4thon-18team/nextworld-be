package com.likelion.nextworld.domain.scrap.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.nextworld.domain.scrap.dto.ScrapResponse;
import com.likelion.nextworld.domain.scrap.entity.Scrap;

@Component
public class ScrapMapper {

  public ScrapResponse toResponse(Scrap s) {
    String type = s.isWorkTarget() ? "WORK" : "POST";
    Long targetId = s.isWorkTarget() ? s.getWork().getId() : s.getPost().getId();
    String title = s.isWorkTarget() ? s.getWork().getTitle() : s.getPost().getTitle();

    return ScrapResponse.builder()
        .id(s.getId())
        .targetType(type)
        .targetId(targetId)
        .title(title)
        .createdAt(s.getCreatedAt())
        .build();
  }

  public List<ScrapResponse> toResponseList(List<Scrap> scraps) {
    return scraps.stream().map(this::toResponse).toList();
  }
}
