package com.likelion.nextworld.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class CommentRequest {

  @Getter
  public static class Create {

    @Schema(description = "댓글 내용", example = "정말 멋진 작품이네요!")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글 작성 시 전달, 일반 댓글은 null)", example = "15", nullable = true)
    private Long parentCommentId;
  }

  @Getter
  public static class Update {

    @Schema(description = "수정할 댓글 내용", example = "내용을 조금 수정했습니다!")
    private String content;
  }
}
