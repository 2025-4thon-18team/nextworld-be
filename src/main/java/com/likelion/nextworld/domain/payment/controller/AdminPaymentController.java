package com.likelion.nextworld.domain.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.service.AdminPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

  private final AdminPaymentService adminPaymentService;

  @GetMapping
  public ResponseEntity<List<PayItemResponse>> getRefundRequests() {
    return ResponseEntity.ok(adminPaymentService.getRefundRequests());
  }

  @PatchMapping("/{payId}/refund")
  public ResponseEntity<String> approveRefund(@PathVariable Long payId) {
    adminPaymentService.approveRefund(payId);
    return ResponseEntity.ok("환불이 완료되었습니다.");
  }
}
