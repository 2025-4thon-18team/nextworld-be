package com.likelion.nextworld.domain.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Validated
public class TestController {

  // ✅ 누구나 접근 가능: 200 + BaseResponse 형식
  @Operation(summary = "Permit-All 테스트", description = "인증 없이 접근 가능")
  @GetMapping("/permit-all")
  public ResponseEntity<BaseResponse<Void>> permitAll() {
    return ResponseEntity.ok(BaseResponse.success(null));
  }
}
