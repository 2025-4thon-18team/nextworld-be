package com.likelion.nextworld.domain.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.payment.dto.*;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 관리 API")
@SecurityRequirement(name = "Authorization")
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "포인트 충전", description = "아임포트를 통해 포인트를 충전합니다.")
  @PostMapping("/charge")
  public ResponseEntity<Void> charge(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody ChargeRequest req) {
    paymentService.charge(user, req);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "결제 검증", description = "아임포트 결제를 검증합니다.")
  @PostMapping("/verify")
  public ResponseEntity<Boolean> verify(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody VerifyRequest req) {
    boolean ok = paymentService.verify(user, req);
    return ResponseEntity.ok(ok);
  }

  @Operation(summary = "포인트 사용", description = "포인트를 사용하여 작품을 구매합니다.")
  @PostMapping("/use")
  public ResponseEntity<Void> use(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody UseRequest req) {
    paymentService.use(user, req);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "환불 요청", description = "결제한 작품에 대한 환불을 요청합니다.")
  @PostMapping("/refund")
  public ResponseEntity<String> requestRefund(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody RefundRequest request) {
    paymentService.requestRefund(request, user.getId());
    return ResponseEntity.ok("환불 요청이 접수되었습니다. 관리자의 승인을 기다려주세요.");
  }

  @Operation(summary = "충전 옵션 조회", description = "포인트 충전 옵션을 조회합니다.")
  @GetMapping("/options")
  public ResponseEntity<ChargeOptionsResponse> getChargeOptions(
      @AuthenticationPrincipal UserPrincipal user) {
    ChargeOptionsResponse response = paymentService.getChargeOptions(user);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "충전 내역 조회", description = "사용자의 포인트 충전 내역을 조회합니다.")
  @GetMapping("/history/charge")
  public ResponseEntity<List<PaymentHistoryResponse>> getChargeHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<PaymentHistoryResponse> response = paymentService.getChargeHistory(user);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "사용 내역 조회", description = "사용자의 포인트 사용 내역을 조회합니다.")
  @GetMapping("/history/use")
  public ResponseEntity<List<PaymentHistoryResponse>> getUseHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<PaymentHistoryResponse> response = paymentService.getUseHistory(user);
    return ResponseEntity.ok(response);
  }
}
