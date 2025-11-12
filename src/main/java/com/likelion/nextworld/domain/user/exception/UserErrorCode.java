package com.likelion.nextworld.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.likelion.nextworld.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  USER_NOT_FOUND("USER_0001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_UNAUTHORIZED("USER_0002", "사용자 인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
  USER_FORBIDDEN("USER_0003", "해당 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
