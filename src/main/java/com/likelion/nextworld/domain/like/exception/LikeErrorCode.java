package com.likelion.nextworld.domain.like.exception;

import org.springframework.http.HttpStatus;

import com.likelion.nextworld.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeErrorCode implements BaseErrorCode {
  LIKE_NOT_FOUND("LIKE_0001", "좋아요 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ALREADY_LIKED("LIKE_0002", "이미 좋아요를 누른 상태입니다.", HttpStatus.BAD_REQUEST),
  LIKE_SAVE_FAILED("LIKE_0003", "좋아요 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  LIKE_DELETE_FAILED("LIKE_0004", "좋아요 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
