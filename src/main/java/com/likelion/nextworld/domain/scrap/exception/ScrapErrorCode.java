package com.likelion.nextworld.domain.scrap.exception;

import org.springframework.http.HttpStatus;

import com.likelion.nextworld.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScrapErrorCode implements BaseErrorCode {
  SCRAP_NOT_FOUND("SCRAP_0001", "스크랩을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ALREADY_SCRAPPED("SCRAP_0002", "이미 스크랩한 대상입니다.", HttpStatus.BAD_REQUEST),
  SCRAP_SAVE_FAILED("SCRAP_0003", "스크랩 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  SCRAP_DELETE_FAILED("SCRAP_0004", "스크랩 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
