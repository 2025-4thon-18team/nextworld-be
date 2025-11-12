package com.likelion.nextworld.domain.admin.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.service.AdminPaymentService;
import com.likelion.nextworld.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

  private final AdminPaymentService adminPaymentService;

  @Operation(summary = "환불 요청 목록 조회", description = "관리자가 처리해야 할 환불 요청 리스트를 반환합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<PayItemResponse>>> getRefundRequests() {

    List<PayItemResponse> refundList = adminPaymentService.getRefundRequests();

    return ResponseEntity.ok(BaseResponse.success("환불 요청 목록 조회 완료", refundList));
  }

  @Operation(summary = "환불 승인", description = "관리자가 특정 결제의 환불을 승인합니다.")
  @PatchMapping("/{payId}/refund")
  public ResponseEntity<BaseResponse<Void>> approveRefund(@PathVariable Long payId) {

    adminPaymentService.approveRefund(payId);

    return ResponseEntity.ok(BaseResponse.success("환불이 성공적으로 처리되었습니다.", null));
  }
}
