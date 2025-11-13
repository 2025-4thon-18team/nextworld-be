package com.likelion.nextworld.domain.post.exception;

import org.springframework.http.HttpStatus;

import com.likelion.nextworld.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {
  COMMENT_NOT_FOUND("COMMENT_0001", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_FORBIDDEN("COMMENT_0002", "댓글 수정 권한이 없습니다.", HttpStatus.FORBIDDEN),
  COMMENT_SAVE_FAILED("COMMENT_0003", "댓글 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
