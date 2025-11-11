package com.likelion.nextworld.domain.post.dto;

import com.likelion.nextworld.domain.post.entity.CreationType;
import com.likelion.nextworld.domain.post.entity.PostType;
import com.likelion.nextworld.domain.post.entity.WorkStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
  private String title;
  private String content;
  private Boolean hasImage; // 이미지 포함 여부

  private Long workId; // 작품 회차인 경우 소속 작품 ID
  private PostType postType; // POST, EPISODE
  private Integer episodeNumber; // 회차 번호

  private Long parentWorkId; // 원작 참조 (원작 작품 지정)
  private CreationType creationType; // ORIGINAL, DERIVATIVE (NULL 가능)

  private Boolean isPaid;
  private Long price;

  private String tags; // 구분자 문자열: "태그1|태그2|태그3"

  private WorkStatus status;
}
