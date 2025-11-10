package com.likelion.nextworld.domain.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.payment.dto.*;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/charge")
  public ResponseEntity<Void> charge(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody ChargeRequest req) {
    paymentService.charge(user, req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/verify")
  public ResponseEntity<Boolean> verify(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody VerifyRequest req) {
    boolean ok = paymentService.verify(user, req);
    return ResponseEntity.ok(ok);
  }

  @PostMapping("/use")
  public ResponseEntity<Void> use(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody UseRequest req) {
    paymentService.use(user, req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refund")
  public ResponseEntity<String> requestRefund(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody RefundRequest request) {
    paymentService.requestRefund(request, user.getId());
    return ResponseEntity.ok("환불 요청이 접수되었습니다. 관리자의 승인을 기다려주세요.");
  }

  @GetMapping("/options")
  public ResponseEntity<ChargeOptionsResponse> getChargeOptions(
      @AuthenticationPrincipal UserPrincipal user) {
    ChargeOptionsResponse response = paymentService.getChargeOptions(user);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/history/charge")
  public ResponseEntity<List<PaymentHistoryResponse>> getChargeHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<PaymentHistoryResponse> response = paymentService.getChargeHistory(user);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/history/use")
  public ResponseEntity<List<PaymentHistoryResponse>> getUseHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<PaymentHistoryResponse> response = paymentService.getUseHistory(user);
    return ResponseEntity.ok(response);
  }
}
