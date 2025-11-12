package com.likelion.nextworld.domain.post.exception;

import org.springframework.http.HttpStatus;

import com.likelion.nextworld.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkErrorCode implements BaseErrorCode {
  WORK_NOT_FOUND("WORK_0001", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  WORK_NOT_ACCESSIBLE("WORK_0002", "해당 게시글에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
  WORK_SAVE_FAILED("WORK_0003", "게시글 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  WORK_DELETE_FAILED("WORK_0004", "게시글 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
