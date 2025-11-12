package com.likelion.nextworld.domain.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.service.AdminPaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@Tag(name = "Admin Payment", description = "관리자 결제 관리 API")
@SecurityRequirement(name = "Authorization")
public class AdminPaymentController {

  private final AdminPaymentService adminPaymentService;

  @Operation(summary = "환불 요청 목록 조회", description = "관리자가 환불 요청 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<PayItemResponse>> getRefundRequests() {
    return ResponseEntity.ok(adminPaymentService.getRefundRequests());
  }

  @Operation(summary = "환불 승인", description = "관리자가 환불 요청을 승인합니다.")
  @PatchMapping("/{payId}/refund")
  public ResponseEntity<String> approveRefund(
      @Parameter(description = "결제 ID", required = true) @PathVariable Long payId) {
    adminPaymentService.approveRefund(payId);
    return ResponseEntity.ok("환불이 완료되었습니다.");
  }
}
